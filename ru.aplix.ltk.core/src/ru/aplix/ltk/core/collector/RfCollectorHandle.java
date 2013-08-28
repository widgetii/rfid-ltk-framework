package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.reader.RfReaderStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgServiceHandle;


/**
 * RFID collector subscription handle.
 *
 * <p>Other types of collector messages can be subscribed for through this
 * handle.</p>
 */
public final class RfCollectorHandle
		extends MsgServiceHandle<RfCollectorHandle, RfReaderStatusMessage> {

	private final RfCollector collector;

	RfCollectorHandle(
			RfCollector collector,
			MsgConsumer<
				? super RfCollectorHandle,
				? super RfReaderStatusMessage> consumer) {
		super(collector, consumer);
		this.collector = collector;
	}

	/**
	 * Request the reporting of changes in RFID tag appearance.
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

}
