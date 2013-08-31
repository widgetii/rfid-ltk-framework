package ru.aplix.ltk.driver.ctg.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.llrp.ltk.generated.enumerations.*;
import org.llrp.ltk.generated.messages.*;
import org.llrp.ltk.generated.parameters.*;
import org.llrp.ltk.net.LLRPConnector;
import org.llrp.ltk.net.LLRPEndpoint;
import org.llrp.ltk.types.*;

import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.core.reader.RfReaderError;


final class CtgReaderThread
		extends Thread
		implements UncaughtExceptionHandler, LLRPEndpoint {

	private final CtgRfReaderDriver driver;
	private volatile boolean connected;
	private volatile boolean stopped;
	private LLRPConnector reader;

	public CtgReaderThread(CtgRfReaderDriver driver) {
		super("CtgRfReader");
		this.driver = driver;
		setUncaughtExceptionHandler(this);
	}

	public final CtgRfReaderDriver getDriver() {
		return this.driver;
	}

	public final RfReaderContext getContext() {
		return getDriver().getContext();
	}

	public final CtgRfConfig getConfig() {
		return getDriver().getConfig();
	}

	@Override
	public void run() {
		this.reader = new LLRPConnector(
				this,
				getConfig().getReaderHost(),
				getConfig().getReaderPort());
		for (;;) {
			if (this.connected) {
				if (waitForTermination()) {
					break;
				}
				continue;
			}
			if (connect()) {
				this.connected = true;
				continue;
			}
			if (!reconnectionDelay()) {
				break;
			}
		}

		interrupt();
		disconnect();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		sendError(e);
	}

	@Override
	public void messageReceived(LLRPMessage message) {
		try {

			final short messageType = message.getTypeNum().toShort();

			if (messageType == RO_ACCESS_REPORT.TYPENUM.toShort()) {
				tagRecived(message);
			}
		} catch (Throwable e) {
			sendError(e);
		}
	}

	@Override
	public void errorOccured(String message) {
		getContext().updateStatus(new RfReaderError(null, message));
	}

	public synchronized void stopReader() {
		this.stopped = true;
		notifyAll();
	}

	private boolean connect() {
		try {
			this.reader.connect();
			if (this.stopped) {
				return false;
			}
			return (deleteROSpecs()
					&& addROSpec()
					&& enableROSpec()
					&& startROSpec());
		} catch (Throwable e) {
			sendError(e);
		}
		return false;
	}

	private boolean reconnectionDelay() {

		long left = getConfig().getReconnectionDelay();
		final long end = System.currentTimeMillis() + left;

		for (;;) {
			synchronized (this) {
				if (this.stopped) {
					return false;
				}
				if (end <= 0) {
					return true;
				}
				try {
					wait(left);
				} catch (InterruptedException e) {
					return false;
				}
			}
			left = end - System.currentTimeMillis();
		}
	}

	private synchronized boolean waitForTermination() {
		try {
			wait();
		} catch (InterruptedException e) {
			return true;
		}
		return this.stopped;
	}

	private boolean deleteROSpecs() throws TimeoutException {

		final DELETE_ROSPEC request = new DELETE_ROSPEC();
		// Use zero as the ROSpec ID.
		// This means delete all ROSpecs.
		request.setROSpecID(new UnsignedInteger(0));

		final DELETE_ROSPEC_RESPONSE response =
				(DELETE_ROSPEC_RESPONSE) this.reader.transact(
						request,
						getConfig().getTransactionTimeout());

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
		roSpec.setROSpecID(new UnsignedInteger(getConfig().getROSpecId()));

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
		reportContent.setEnableAntennaID(new Bit(0));
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

	// Add the ROSpec to the reader.
	private boolean addROSpec() throws TimeoutException {

		final ADD_ROSPEC request = new ADD_ROSPEC();

		request.setROSpec(buildROSpec());

		final ADD_ROSPEC_RESPONSE response =
				(ADD_ROSPEC_RESPONSE) this.reader.transact(
						request,
						getConfig().getTransactionTimeout());

		// Check if the we successfully added the ROSpec.
		return checkStatus(response.getLLRPStatus());
	}

	// Enable the ROSpec.
	private boolean enableROSpec() throws TimeoutException {

		final ENABLE_ROSPEC request = new ENABLE_ROSPEC();

		request.setROSpecID(new UnsignedInteger(getConfig().getROSpecId()));

		final ENABLE_ROSPEC_RESPONSE response =
				(ENABLE_ROSPEC_RESPONSE) this.reader.transact(
						request,
						getConfig().getTransactionTimeout());

		return checkStatus(response.getLLRPStatus());
	}

	// Start the ROSpec.
	private boolean startROSpec() throws TimeoutException {

		final START_ROSPEC request = new START_ROSPEC();

		request.setROSpecID(new UnsignedInteger(getConfig().getROSpecId()));

		final START_ROSPEC_RESPONSE response =
				(START_ROSPEC_RESPONSE) this.reader.transact(
						request,
						getConfig().getTransactionTimeout());

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

		getContext().updateStatus(new RfReaderError(null, errorMessage));

		return false;
	}

	private void tagRecived(LLRPMessage message) {
		// The message received is an Access Report.
		final RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) message;
		// Get a list of the tags read.
		final List<TagReportData> tags = report.getTagReportDataList();

		// Loop through the list and get the EPC of each tag.
		for (TagReportData tag : tags) {
			try {
				getContext().sendData(new LLRPDataMessage(tag));
			} catch (Throwable e) {
				sendError(e);
			}
		}
	}

	private void sendError(Throwable cause) {
		getContext().updateStatus(new RfReaderError(null, cause));
	}

	private void disconnect() {
		if (this.reader != null) {
			this.reader.disconnect();
			this.reader = null;
		}
	}

}