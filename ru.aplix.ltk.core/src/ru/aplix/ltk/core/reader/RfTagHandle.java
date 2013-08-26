package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgHandle;
import ru.aplix.ltk.message.MsgSubscriptions;


/**
 * RFID tag messages subscription handle.
 *
 * <p>Subscription can be made by {@link RfReaderHandle#requestTags(
 * MsgConsumer)} method.<p>
 */
public class RfTagHandle extends MsgHandle<RfTagHandle, RfTagMessage> {

	RfTagHandle(
			MsgSubscriptions<RfTagHandle, RfTagMessage> subscriptions,
			MsgConsumer<? super RfTagHandle, ? super RfTagMessage> consumer) {
		super(subscriptions, consumer);
	}

}
