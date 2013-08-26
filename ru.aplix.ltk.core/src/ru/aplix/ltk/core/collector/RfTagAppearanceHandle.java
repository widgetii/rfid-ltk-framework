package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgHandle;


/**
 * Subscription handle for change in RFID tag appearance.
 */
public final class RfTagAppearanceHandle
		extends MsgHandle<RfTagAppearanceHandle, RfTagAppearanceMessage> {

	RfTagAppearanceHandle(
			RfCollector collector,
			MsgConsumer<
				? super RfTagAppearanceHandle,
				? super RfTagAppearanceMessage> consumer) {
		super(collector.tagAppearanceSubscriptions(), consumer);
	}

}
