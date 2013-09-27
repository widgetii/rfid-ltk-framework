package ru.aplix.ltk.core.collector;

import static java.util.Objects.requireNonNull;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgService;
import ru.aplix.ltk.message.MsgSubscriptions;


/**
 * RFID tags collector.
 *
 * <p>Collects RFID tags information from the given {@link RfSource RFID data
 * source}, and informs subscribers on tag visibility by that source, such as
 * tag appearance and disappearance within RFID reader's field of view (FOV).
 * </p>
 */
public class RfCollector
		extends MsgService<RfCollectorHandle, RfStatusMessage> {

	private final RfTagAppearanceSubscriptions tagAppearanceSubscriptions =
			new RfTagAppearanceSubscriptions(this);
	private final RfSource source;
	private final RfSourceListener sourceListener;

	/**
	 * Constructs collector with a default RFID tags tracker.
	 *
	 * @param source RFID data source to collect data from.
	 */
	public RfCollector(RfSource source) {
		this(source, (RfTracker) null);
	}

	/**
	 * Constructs collector with the given tracker.
	 *
	 * @param source RFID data source to collect data from.
	 * @param tracker RFID tags tracker, or <code>null</code> to construct a
	 * {@link DefaultRfTracker default one}.
	 */
	public RfCollector(RfSource source, RfTracker tracker) {
		requireNonNull(source, "RFID data source not specified");
		this.source = source;
		this.sourceListener = new RfSourceListener(this, tracker);
	}

	/**
	 * Constructs collector with the given tracking policy.
	 *
	 * @param source RFID data source to collect data from.
	 * @param trackingPolicy RFID tracking policy, or <code>null</code> to use
	 * the {@link RfTrackingPolicy#DEFAULT_TRACKING_POLICY default one}.
	 */
	public RfCollector(RfSource source, RfTrackingPolicy trackingPolicy) {
		requireNonNull(source, "RFID data source not specified");
		this.source = source;
		this.sourceListener = new RfSourceListener(this, trackingPolicy);
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
		this.source = reader.toRfSource();
		this.sourceListener = new RfSourceListener(this, tracker);
	}

	/**
	 * RFID source the data is collected from.
	 *
	 * @return the source passed to constructor.
	 */
	protected final RfSource source() {
		return this.source;
	}

	/**
	 * RFID tags tracker.
	 *
	 * @return the tracker passed to constructor, or the default one if it were
	 * not specified.
	 */
	protected final RfTracker tracker() {
		return this.sourceListener.tracker();
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
					? super RfStatusMessage> consumer) {
		return new RfCollectorHandle(this, consumer);
	}

	/**
	 * Constructs a tag appearance message consumer to receive messages since
	 * one with the given event identifier.
	 *
	 * <p>This method is called when historical message reporting
	 * {@link RfCollectorHandle#requestTagAppearance(MsgConsumer, long)
	 * requested}. The returned consumer will be subscribed to receive messages
	 * instead of original one.</p>
	 *
	 * <p>The constructed consumer is responsible for sending messages to the
	 * given one. It may, however, delay the reporting of new messages until
	 * all requested messages reported.</p>
	 *
	 * <p>Returns the same consumer by default.</p>
	 *
	 * @param consumer consumer to report changes in tag appearance to.
	 * @param lastEventId the last event identifier known to the consumer.
	 * Always positive number.
	 *
	 * @return consumer to subscribe on changes in tag appearance.
	 */
	protected MsgConsumer<
			? super RfTagAppearanceHandle,
			? super RfTagAppearanceMessage> requestTagAppearance(
					MsgConsumer<
							? super RfTagAppearanceHandle,
							? super RfTagAppearanceMessage> consumer,
					long lastEventId) {
		return consumer;
	}

	@Override
	protected void startService() {
		this.sourceListener.requestStatus();
	}

	@Override
	protected void subscribed(RfCollectorHandle handle) {
		super.subscribed(handle);

		final RfStatusMessage lastStatus = this.sourceListener.lastStatus();

		if (lastStatus != null) {
			handle.getConsumer().messageReceived(lastStatus);
		}
	}

	@Override
	protected void stopService() {
		this.sourceListener.rejectStatus();
	}

	final MsgSubscriptions<RfCollectorHandle, RfStatusMessage>
	collectorSubscriptions() {
		return serviceSubscriptions();
	}

	final void noError() {
		this.sourceListener.noError();
	}

	final void updateStatus(RfStatusMessage status) {
		this.sourceListener.updateStatus(status);
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
			this.collector.sourceListener.requestData();
		}

		@Override
		protected void lastUnsubscribed(RfTagAppearanceHandle handle) {
			this.collector.sourceListener.rejectData();
			super.lastUnsubscribed(handle);
		}

	}

}
