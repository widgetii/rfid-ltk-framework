package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgHandle;
import ru.aplix.ltk.message.MsgSubscriptions;


/**
 * RFID read operation messages subscription handle.
 *
 * <p>Subscription can be made by {@link RfReaderHandle#read(MsgConsumer)}
 * method.<p>
 */
public class RfReadHandle extends MsgHandle<RfReadHandle, RfReadMessage> {

	RfReadHandle(
			MsgSubscriptions<RfReadHandle, RfReadMessage> subscriptions,
			MsgConsumer<? super RfReadHandle, ? super RfReadMessage> consumer) {
		super(subscriptions, consumer);
	}

}
