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

	private final RfTagSubscriptions tagSubscriptions =
			new RfTagSubscriptions();
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
	 * {@link RfReaderDriver#getRefPortId() driver}.
	 */
	public final String getRfPortId() {
		return driver().getRefPortId();
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

	/**
	 * RFID driver to use.
	 *
	 * @return driver instance passed to this service constructor.
	 */
	protected final RfReaderDriver driver() {
		return this.driver;
	}

	/**
	 * RFID tag messages subscriptions.
	 *
	 * @return subscriptions made through
	 * {@link RfReaderHandle#read(MsgConsumer) reader handle}.
	 */
	protected final
	MsgSubscriptions<RfReadHandle, RfReadMessage> tagSubscriptions() {
		return this.tagSubscriptions;
	}

	final MsgSubscriptions<RfReaderHandle, RfReaderStatusMessage>
	readerSubscriptions() {
		return serviceSubscriptions();
	}

	private static final class RfTagSubscriptions
			extends MsgSubscriptions<RfReadHandle, RfReadMessage> {

		@Override
		protected RfReadHandle createHandle(
				MsgConsumer<
					? super RfReadHandle,
					? super RfReadMessage> consumer) {
			return new RfReadHandle(this, consumer);
		}

	}

}
