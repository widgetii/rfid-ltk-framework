package ru.aplix.ltk.core.collector;

import static java.util.Objects.requireNonNull;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.reader.RfReaderHandle;
import ru.aplix.ltk.core.reader.RfReaderStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgService;
import ru.aplix.ltk.message.MsgSubscriptions;


/**
 * RFID tags collector service.
 *
 * <p>This service collects RFID tags information from the given
 * {@link RfReader RFID reader}, and informs subscribers on tag appearance
 * and disappearance within that reader's field of view (FOV).</p>
 */
public class RfCollector
		extends MsgService<RfCollectorHandle, RfReaderStatusMessage> {

	private final RfTagAppearanceSubscriptions tagAppearanceSubscriptions =
			new RfTagAppearanceSubscriptions(this);
	private final RfReader reader;
	private final RfReaderListener readerListener;
	private final RfTagListener tagListener;

	/**
	 * Constructs collector with a default RFID tags tracker.
	 *
	 * @param reader RFID reader to collect data from.
	 */
	public RfCollector(RfReader reader) {
		this(reader, null);
	}

	/**
	 * Constructs collector.
	 *
	 * @param reader RFID reader to collect data from.
	 * @param tracker RFID tags tracker, or <code>null</code> to construct a
	 * {@link DefaultRfTracker default one}.
	 */
	public RfCollector(RfReader reader, RfTracker tracker) {
		requireNonNull(reader, "RFID reader not specified");
		this.reader = reader;
		this.readerListener = new RfReaderListener(this);
		this.tagListener = new RfTagListener(this, tracker);
	}

	/**
	 * RFID reader's port identifier.
	 *
	 * @return identifier string returned by
	 * {@link RfReader#getRfPortId() RFID reader}.
	 */
	public final String getRfPortId() {
		return reader().getRfPortId();
	}

	/**
	 * RFID reader the data is collected from.
	 *
	 * @return the reader passed to constructor.
	 */
	protected final RfReader reader() {
		return this.reader;
	}

	/**
	 * RFID tags tracker.
	 *
	 * @return the tracker passed to constructor, or the default one if it were
	 * not specified.
	 */
	protected final RfTracker tracker() {
		return this.tagListener.tracker();
	}

	/**
	 * Tag appearance message subscriptions.
	 *
	 * @return subscriptions made through
	 * {@link RfCollectorHandle#requestTagAppearance(MsgConsumer) collector
	 * subscription handle}
	 */
	protected final MsgSubscriptions<
			RfTagAppearanceHandle,
			RfTagAppearanceMessage> tagAppearanceSubscriptions() {
		return this.tagAppearanceSubscriptions;
	}

	@Override
	protected RfCollectorHandle createServiceHandle(
			MsgConsumer<
					? super RfCollectorHandle,
					? super RfReaderStatusMessage> consumer) {
		return new RfCollectorHandle(this, consumer);
	}

	@Override
	protected void startService() {
		this.readerListener.start();
	}

	@Override
	protected void stopService() {
		this.readerListener.stop();
	}

	final MsgSubscriptions<RfCollectorHandle, RfReaderStatusMessage>
	collectorSubscriptions() {
		return serviceSubscriptions();
	}

	final RfReaderHandle readerHandle() {
		return this.readerListener.handle();
	}

	private static final class RfTagAppearanceSubscriptions
			extends MsgSubscriptions<
					RfTagAppearanceHandle,
					RfTagAppearanceMessage> {

		private final RfCollector collector;

		RfTagAppearanceSubscriptions(RfCollector collector) {
			this.collector = collector;
		}

		@Override
		protected RfTagAppearanceHandle createHandle(
				MsgConsumer<
						? super RfTagAppearanceHandle,
						? super RfTagAppearanceMessage> consumer) {
			return new RfTagAppearanceHandle(this.collector, consumer);
		}

		@Override
		protected void firstSubscribed(RfTagAppearanceHandle handle) {
			super.firstSubscribed(handle);
			this.collector.tagListener.start();
		}

		@Override
		protected void lastUnsubscribed(RfTagAppearanceHandle handle) {
			this.collector.tagListener.stop();
			super.lastUnsubscribed(handle);
		}

	}

}
