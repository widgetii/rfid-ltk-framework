package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgServiceHandle;


/**
 * RFID collector subscription handle.
 *
 * <p>Other types of collector messages can be subscribed for through this
 * handle.</p>
 */
public final class RfCollectorHandle
		extends MsgServiceHandle<RfCollectorHandle, RfStatusMessage> {

	private final RfCollector collector;

	RfCollectorHandle(
			RfCollector collector,
			MsgConsumer<
				? super RfCollectorHandle,
				? super RfStatusMessage> consumer) {
		super(collector, consumer);
		this.collector = collector;
	}

	/**
	 * Requests the reporting of changes in RFID tag appearance.
	 *
	 * @param consumer consumer to subscribe on changes in tag appearance.
	 *
	 * @return subscription handle.
	 */
	public final RfTagAppearanceHandle requestTagAppearance(
			MsgConsumer<
					? super RfTagAppearanceHandle,
					? super RfTagAppearanceMessage> consumer) {
		return addSubscription(
				this.collector.tagAppearanceSubscriptions()
				.subscribe(consumer));
	}

	/**
	 * Requests the reporting of changes in RFID tag appearance starting from
	 * the given event identifier.
	 *
	 * <p>The consumer will receive messages with
	 * {@link RfTagAppearanceMessage#getEventId() identifiers} greater than the
	 * {@code lastEventId}, in order.</p>
	 *
	 * <p>Only collectors, which preserve previously reported messages will
	 * report them. And even them can report only messages they preserved.
	 * There is no guarantee that all messages since the given one are
	 * preserved, because messages are not necessarily preserved forever.
	 * The logging collector, for example, can store only a limited number of
	 * messages.</p>
	 *
	 * @param consumer consumer to subscribe on changes in tag appearance.
	 * @param lastEventId the last event identifier known to the consumer, or
	 * zero to request all recorded events, or negative number to subscribe
	 * only to new events.
	 *
	 * @return subscription handle.
	 */
	public final RfTagAppearanceHandle requestTagAppearance(
			MsgConsumer<
					? super RfTagAppearanceHandle,
					? super RfTagAppearanceMessage> consumer,
			long lastEventId) {
		if (lastEventId < 0) {
			return requestTagAppearance(consumer);
		}
		return requestTagAppearance(
				this.collector.requestTagAppearance(
						consumer,
						lastEventId));
	}

}
