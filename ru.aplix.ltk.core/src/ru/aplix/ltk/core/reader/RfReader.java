package ru.aplix.ltk.core.reader;

import static java.util.Objects.requireNonNull;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgService;
import ru.aplix.ltk.message.MsgSubscriptions;


/**
 * RFID reader service.
 */
public class RfReader
		extends MsgService<RfReaderHandle, RfReaderStatusMessage> {

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
	 * @return subscriptions made through the {@link RfReaderHandle#requestData(
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
				? super RfReaderStatusMessage> consumer) {
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

	final MsgSubscriptions<RfReaderHandle, RfReaderStatusMessage>
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

}
