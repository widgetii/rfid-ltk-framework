package ru.aplix.ltk.core.reader;

import static java.util.Objects.requireNonNull;
import static ru.aplix.ltk.core.util.IntSet.FULL_INT_SET;
import ru.aplix.ltk.core.source.*;
import ru.aplix.ltk.core.util.IntSet;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgService;
import ru.aplix.ltk.message.MsgSubscriptions;


/**
 * RFID reader interface.
 */
public class RfReader extends MsgService<RfReaderHandle, RfStatusMessage> {

	private final RfDataSubscriptions dataSubscriptions =
			new RfDataSubscriptions();
	private final RfReaderDriver driver;
	private boolean driverInitialized;

	/**
	 * Constructs the reader service with the given driver.
	 *
	 * @param driver RFID reader driver to use.
	 */
	public RfReader(RfReaderDriver driver) {
		requireNonNull(driver, "RFID reader driver not specified");
		this.driver = driver;
	}

	/**
	 * RFID reader's port identifier.
	 *
	 * @return identifier string returned by
	 * {@link RfReaderDriver#getRfPortId() driver}.
	 */
	public final String getRfPortId() {
		return driver().getRfPortId();
	}

	/**
	 * Converts this reader to RFID data source.
	 *
	 * @return new RFID data source receiving data from this reader.
	 */
	public RfSource toRfSource() {
		return new RfReaderSource(this, FULL_INT_SET);
	}

	/**
	 * Converts this reader to filtering RFID data source.
	 *
	 * @param antennas a set of integer antenna identifiers to read data from.
	 *
	 * @return new RFID data source receiving data from the given set of
	 * antennas of this reader.
	 */
	public RfSource toRfSource(IntSet antennas) {
		return new RfReaderSource(this, antennas);
	}

	/**
	 * RFID driver used.
	 *
	 * @return driver instance passed to this service constructor.
	 */
	protected final RfReaderDriver driver() {
		return this.driver;
	}

	/**
	 * RFID reader data subscriptions.
	 *
	 * @return subscriptions made through the {@link RfReaderHandle#requestRfData(
	 * MsgConsumer) reader subscription handle}.
	 */
	protected final
	MsgSubscriptions<RfDataHandle, RfDataMessage> dataSubscriptions() {
		return this.dataSubscriptions;
	}

	@Override
	protected RfReaderHandle createServiceHandle(
			MsgConsumer<
				? super RfReaderHandle,
				? super RfStatusMessage> consumer) {
		return new RfReaderHandle(this, consumer);
	}

	@Override
	protected void startService() {
		if (!this.driverInitialized) {
			driver().initRfReader(new RfReaderContext(this));
			this.driverInitialized = true;
		}
		driver().startRfReader();
	}

	@Override
	protected void stopService() {
		driver().stopRfReader();
	}

	final MsgSubscriptions<RfReaderHandle, RfStatusMessage>
	readerSubscriptions() {
		return serviceSubscriptions();
	}

	private static final class RfDataSubscriptions
			extends MsgSubscriptions<RfDataHandle, RfDataMessage> {

		@Override
		protected RfDataHandle createHandle(
				MsgConsumer<
					? super RfDataHandle,
					? super RfDataMessage> consumer) {
			return new RfDataHandle(this, consumer);
		}

	}

	private static final class RfReaderSource implements RfSource {

		private final RfReader reader;
		private final IntSet antennas;
		private RfReaderHandle readerHandle;
		private RfDataHandle dataHandle;

		RfReaderSource(RfReader reader, IntSet antennas) {
			this.reader = reader;
			this.antennas = antennas;
		}

		@Override
		public void requestRfStatus(RfStatusUpdater updater) {
			this.readerHandle = this.reader.subscribe(
					new RfReaderStatusListener(updater));
		}

		@Override
		public void requestRfData(RfDataReceiver receiver) {
			this.dataHandle = this.readerHandle.requestRfData(
					new RfReaderDataListener(receiver, this.antennas));
		}

		@Override
		public void rejectRfData() {
			this.dataHandle.unsubscribe();
			this.dataHandle = null;
		}

		@Override
		public void rejectRfStatus() {
			this.readerHandle.unsubscribe();
			this.readerHandle = null;
		}

	}

	private static final class RfReaderStatusListener
			implements MsgConsumer<RfReaderHandle, RfStatusMessage> {

		private final RfStatusUpdater statusUdater;

		RfReaderStatusListener(RfStatusUpdater statusUdater) {
			this.statusUdater = statusUdater;
		}

		@Override
		public void consumerSubscribed(RfReaderHandle handle) {
		}

		@Override
		public void messageReceived(RfStatusMessage message) {
			this.statusUdater.updateStatus(message);
		}

		@Override
		public void consumerUnsubscribed(RfReaderHandle handle) {
		}

	}

	private static final class RfReaderDataListener
			implements MsgConsumer<RfDataHandle, RfDataMessage> {

		private final RfDataReceiver receiver;
		private final IntSet antennas;

		RfReaderDataListener(
				RfDataReceiver receiver,
				IntSet antennas) {
			this.receiver = receiver;
			this.antennas = antennas;
		}

		@Override
		public void consumerSubscribed(RfDataHandle handle) {
		}

		@Override
		public void messageReceived(RfDataMessage message) {
			if (this.antennas.contains(message.getAntennaId())) {
				this.receiver.sendRfData(message);
			}
		}

		@Override
		public void consumerUnsubscribed(RfDataHandle handle) {
		}

	}

}
