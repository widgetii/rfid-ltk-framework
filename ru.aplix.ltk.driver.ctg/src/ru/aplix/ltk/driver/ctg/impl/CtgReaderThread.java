package ru.aplix.ltk.driver.ctg.impl;

import static java.lang.System.currentTimeMillis;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.mina.common.IoSession;
import org.llrp.ltk.generated.enumerations.*;
import org.llrp.ltk.generated.messages.*;
import org.llrp.ltk.generated.parameters.*;
import org.llrp.ltk.net.LLRPConnector;
import org.llrp.ltk.net.LLRPEndpoint;
import org.llrp.ltk.net.LLRPIoHandlerAdapterImpl;
import org.llrp.ltk.types.*;

import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.core.source.RfConnected;
import ru.aplix.ltk.core.source.RfError;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;
import ru.aplix.ltk.osgi.Logger;


final class CtgReaderThread
		extends Thread
		implements UncaughtExceptionHandler, LLRPEndpoint {

	private final CtgRfReaderDriver driver;
	private LLRPConnector reader;
	private volatile long lastUpdate;
	private volatile boolean connected;
	private volatile boolean stopped;
	private AntennaID lastAntennaID;

	public CtgReaderThread(CtgRfReaderDriver driver) {
		super("CtgRfReader[" + driver.getRfPortId() + ']');
		this.driver = driver;
		setUncaughtExceptionHandler(this);
	}

	public final CtgRfReaderDriver getDriver() {
		return this.driver;
	}

	public final RfReaderContext getContext() {
		return getDriver().getContext();
	}

	public final CtgRfSettings getSettings() {
		return getDriver().getSettings();
	}

	public final Logger getLogger() {
		return getDriver().getConnection().getProvider().getLogger();
	}

	@Override
	public void run() {
		getLogger().info(this + " Creating LLRP connector: " + getSettings());
		this.reader = new LLRPConnector(
				this,
				getSettings().getReaderHost(),
				getSettings().getReaderPort());
		this.reader.setHandler(new LLRPHandler(this.reader));
		for (;;) {
			if (this.connected) {
				if (!waitForInput()) {
					break;
				}
				continue;
			}
			if (connect()) {
				update();
				this.connected = true;
				getLogger().debug(this + " Connected");
				getContext().updateStatus(
						new RfConnected(getDriver().getRfPortId()));
				continue;
			}
			if (!reconnectionDelay()) {
				break;
			}
		}

		disconnect();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		sendError(e);
	}

	@Override
	public void messageReceived(LLRPMessage message) {
		try {
			update();

			final short messageType = message.getTypeNum().toShort();

			if (!this.connected) {
				getLogger().warning(
						this
						+ " Message received before connection established."
						+ " Message type: " + messageType);
				return;
			}
			if (messageType == RO_ACCESS_REPORT.TYPENUM.toShort()) {
				tagRecived(message);
			} else {
				getLogger().debug(
						this + " Message received. Message type: "
						+ messageType);
			}
		} catch (Throwable e) {
			sendError(e);
		}
	}

	@Override
	public void errorOccured(String message) {
		getContext().updateStatus(new RfError(null, message));
	}

	@Override
	public String toString() {
		if (this.driver == null) {
			return super.toString();
		}
		return "CtgRfReader[" + getDriver().getRfPortId() + ']';
	}

	final AntennaID antennaID(TagReportData tag) {

		final AntennaID antennaID = tag.getAntennaID();

		if (antennaID == null) {
			return this.lastAntennaID;
		}

		return this.lastAntennaID = antennaID;
	}

	public synchronized void stopReader() {
		this.stopped = true;
		notifyAll();
	}

	private boolean connect() {
		try {
			getLogger().debug(this + " Connecting");
			this.reader.connect(getSettings().getConnectionTimeout());
			if (this.stopped) {
				this.reader.disconnect();
				return false;
			}

			boolean ok = false;

			try {
				ok = setReaderConfig()
						&& deleteROSpecs()
						&& addROSpec()
						&& enableROSpec()
						&& startROSpec();
			} finally {
				if (!ok) {
					this.reader.disconnect();
				}
			}

			return ok;
		} catch (Throwable e) {
			sendError(e);
		}
		return false;
	}

	private synchronized boolean waitForInput() {

		final long lastUpdate = this.lastUpdate;
		final int keepAliveTimeout =
				getSettings().getKeepAliveRequestPeriod() * 3;
		final long delay =
				keepAliveTimeout - (currentTimeMillis() - lastUpdate);

		if (!delay(delay)) {
			return false;
		}

		if (this.lastUpdate == lastUpdate) {
			getLogger().error(this + " Keep alive timed out");
			getContext().updateStatus(
					new RfError(null, "Connection lost"));
			this.reader.disconnect();
			this.connected = false;
		}

		return !this.stopped;
	}

	private boolean reconnectionDelay() {

		final long reconnectionDelay = getSettings().getReconnectionDelay();

		getLogger().debug(
				this + " Delay before reconnection "
				+ reconnectionDelay + "ms");

		return delay(reconnectionDelay);
	}

	private boolean delay(long delay) {

		long left = delay;
		final long end = currentTimeMillis() + left;

		for (;;) {
			synchronized (this) {
				if (this.stopped) {
					return false;
				}
				if (left <= 0) {
					return true;
				}
				try {
					wait(left);
				} catch (InterruptedException e) {
					return false;
				}
			}
			left = end - currentTimeMillis();
		}
	}

	private final void update() {
		this.lastUpdate = currentTimeMillis();
	}

	private boolean deleteROSpecs() throws TimeoutException {

		final DELETE_ROSPEC request = new DELETE_ROSPEC();
		final int roSpecId;

		if (getSettings().isExclusiveMode()) {
			// Use zero as the ROSpec ID.
			// This means delete all ROSpecs.
			roSpecId = 0;
		} else {
			roSpecId = getSettings().getROSpecId();
		}

		request.setROSpecID(new UnsignedInteger(roSpecId));

		final DELETE_ROSPEC_RESPONSE response =
				(DELETE_ROSPEC_RESPONSE) this.reader.transact(
						request,
						getSettings().getTransactionTimeout());

		if (roSpecId != 0) {
			return true;
		}

		return checkStatus(response.getLLRPStatus());
	}

	// Build the ROSpec.
	// An ROSpec specifies start and stop triggers,
	// tag report fields, antennas, etc.
	private ROSpec buildROSpec() {
		// Create a Reader Operation Spec (ROSpec).
		final ROSpec roSpec = new ROSpec();

		roSpec.setPriority(new UnsignedByte(0));
		roSpec.setCurrentState(new ROSpecState(ROSpecState.Disabled));
		roSpec.setROSpecID(new UnsignedInteger(getSettings().getROSpecId()));

		// Set up the ROBoundarySpec
		// This defines the start and stop triggers.
		final ROBoundarySpec roBoundarySpec = new ROBoundarySpec();

		// Set the start trigger to null.
		// This means the ROSpec will start as soon as it is enabled.
		final ROSpecStartTrigger startTrig = new ROSpecStartTrigger();

		startTrig.setROSpecStartTriggerType(new ROSpecStartTriggerType(
				ROSpecStartTriggerType.Null));
		roBoundarySpec.setROSpecStartTrigger(startTrig);

		// Set the stop trigger is null. This means the ROSpec
		// will keep running until an STOP_ROSPEC message is sent.
		final ROSpecStopTrigger stopTrig = new ROSpecStopTrigger();

		stopTrig.setDurationTriggerValue(new UnsignedInteger(0));
		stopTrig.setROSpecStopTriggerType(new ROSpecStopTriggerType(
				ROSpecStopTriggerType.Null));
		roBoundarySpec.setROSpecStopTrigger(stopTrig);

		roSpec.setROBoundarySpec(roBoundarySpec);

		// Add an Antenna Inventory Spec (AISpec).
		final AISpec aispec = new AISpec();

		// Set the AI stop trigger to null. This means that
		// the AI spec will run until the ROSpec stops.
		final AISpecStopTrigger aiStopTrigger = new AISpecStopTrigger();

		aiStopTrigger.setAISpecStopTriggerType(new AISpecStopTriggerType(
				AISpecStopTriggerType.Null));
		aiStopTrigger.setDurationTrigger(new UnsignedInteger(0));
		aispec.setAISpecStopTrigger(aiStopTrigger);

		// Select which antenna ports we want to use.
		// Setting this property to zero means all antenna ports.
		final UnsignedShortArray antennaIDs = new UnsignedShortArray();

		antennaIDs.add(new UnsignedShort(0));
		aispec.setAntennaIDs(antennaIDs);

		// Tell the reader that we're reading Gen2 tags.
		final InventoryParameterSpec inventoryParam = new InventoryParameterSpec();

		inventoryParam.setProtocolID(new AirProtocols(
				AirProtocols.EPCGlobalClass1Gen2));
		inventoryParam.setInventoryParameterSpecID(new UnsignedShort(1));
		aispec.addToInventoryParameterSpecList(inventoryParam);

		roSpec.addToSpecParameterList(aispec);

		// Specify what type of tag reports we want
		// to receive and when we want to receive them.
		final ROReportSpec roReportSpec = new ROReportSpec();

		// Receive a report every time a tag is read.
		roReportSpec.setROReportTrigger(new ROReportTriggerType(
				ROReportTriggerType.Upon_N_Tags_Or_End_Of_ROSpec));
		roReportSpec.setN(new UnsignedShort(1));

		final TagReportContentSelector reportContent =
				new TagReportContentSelector();

		// Select which fields we want in the report.
		reportContent.setEnableAccessSpecID(new Bit(0));
		reportContent.setEnableAntennaID(new Bit(1));
		reportContent.setEnableChannelIndex(new Bit(0));
		reportContent.setEnableFirstSeenTimestamp(new Bit(0));
		reportContent.setEnableInventoryParameterSpecID(new Bit(0));
		reportContent.setEnableLastSeenTimestamp(new Bit(1));
		reportContent.setEnablePeakRSSI(new Bit(0));
		reportContent.setEnableROSpecID(new Bit(0));
		reportContent.setEnableSpecIndex(new Bit(0));
		reportContent.setEnableTagSeenCount(new Bit(0));
		roReportSpec.setTagReportContentSelector(reportContent);
		roSpec.setROReportSpec(roReportSpec);

		return roSpec;
	}

	private boolean setReaderConfig() throws TimeoutException {

		final int keepAlivePeriod =
				getSettings().getKeepAliveRequestPeriod();

		final SET_READER_CONFIG request = new SET_READER_CONFIG();
		final KeepaliveSpec keepaliveSpec = new KeepaliveSpec();

		keepaliveSpec.setKeepaliveTriggerType(
				new KeepaliveTriggerType(KeepaliveTriggerType.Periodic));
		keepaliveSpec.setPeriodicTriggerValue(
				new UnsignedInteger(keepAlivePeriod));

		request.setKeepaliveSpec(keepaliveSpec);
		request.setResetToFactoryDefault(new Bit(0));

		final SET_READER_CONFIG_RESPONSE response =
				(SET_READER_CONFIG_RESPONSE) this.reader.transact(
						request,
						getSettings().getTransactionTimeout());

		return checkStatus(response.getLLRPStatus());
	}

	// Add the ROSpec to the reader.
	private boolean addROSpec() throws TimeoutException {

		final ADD_ROSPEC request = new ADD_ROSPEC();

		request.setROSpec(buildROSpec());

		final ADD_ROSPEC_RESPONSE response =
				(ADD_ROSPEC_RESPONSE) this.reader.transact(
						request,
						getSettings().getTransactionTimeout());

		return checkStatus(response.getLLRPStatus());
	}

	// Enable the ROSpec.
	private boolean enableROSpec() throws TimeoutException {

		final ENABLE_ROSPEC request = new ENABLE_ROSPEC();

		request.setROSpecID(new UnsignedInteger(getSettings().getROSpecId()));

		final ENABLE_ROSPEC_RESPONSE response =
				(ENABLE_ROSPEC_RESPONSE) this.reader.transact(
						request,
						getSettings().getTransactionTimeout());

		return checkStatus(response.getLLRPStatus());
	}

	// Start the ROSpec.
	private boolean startROSpec() throws TimeoutException {

		final START_ROSPEC request = new START_ROSPEC();

		request.setROSpecID(new UnsignedInteger(getSettings().getROSpecId()));

		final START_ROSPEC_RESPONSE response =
				(START_ROSPEC_RESPONSE) this.reader.transact(
						request,
						getSettings().getTransactionTimeout());

		return checkStatus(response.getLLRPStatus());
	}

	private boolean checkStatus(LLRPStatus status) {
		if (this.stopped) {
			return false;
		}

		final StatusCode statusCode = status.getStatusCode();

		if (statusCode.intValue() == StatusCode.M_Success) {
			return true;
		}

		final String errorMessage;
		final UTF8String_UTF_8 errorDescription =
				status.getErrorDescription();

		if (errorDescription != null) {
			errorMessage = statusCode + ": " + errorDescription;
		} else {
			errorMessage = statusCode.toString();
		}

		getLogger().error(toString() + ' ' + errorMessage);
		getContext().updateStatus(new RfError(null, errorMessage));

		return false;
	}

	private void tagRecived(LLRPMessage message) {

		final RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) message;
		final List<TagReportData> tags = report.getTagReportDataList();

		getLogger().debug(this + " Tags received: " + tags.size());
		for (TagReportData tag : tags) {
			try {
				getContext().sendRfData(new LLRPDataMessage(this, tag));
			} catch (Throwable e) {
				sendError(e);
			}
		}
	}

	private void sendError(Throwable cause) {
		getLogger().error(toString(), cause);
		getContext().updateStatus(new RfError(null, cause));
	}

	private void disconnect() {
		if (this.reader == null) {
			return;
		}
		try {
			getLogger().info(" Closing connection");
			closeConnection();
		} catch (TimeoutException e) {
			sendError(e);
		} finally {
			this.reader.disconnect();
			this.reader = null;
		}
	}

	private void closeConnection() throws TimeoutException {

		final CLOSE_CONNECTION request = new CLOSE_CONNECTION();

		this.reader.transact(
				request,
				getSettings().getTransactionTimeout());
	}

	private final class LLRPHandler extends LLRPIoHandlerAdapterImpl {

		LLRPHandler(LLRPConnector reader) {
			super(reader);
			setKeepAliveAck(true);
			setKeepAliveForward(true);
		}

		@Override
		public void exceptionCaught(
				IoSession session,
				Throwable cause)
		throws Exception {
			sendError(cause);
		}

	}

}
