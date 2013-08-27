package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgHandle;
import ru.aplix.ltk.message.MsgSubscriptions;


/**
 * RFID reader data subscription handle.
 *
 * <p>Subscription can be made by
 * {@link RfReaderHandle#requestData(MsgConsumer)} method.<p>
 */
public class RfDataHandle extends MsgHandle<RfDataHandle, RfDataMessage> {

	RfDataHandle(
			MsgSubscriptions<RfDataHandle, RfDataMessage> subscriptions,
			MsgConsumer<? super RfDataHandle, ? super RfDataMessage> consumer) {
		super(subscriptions, consumer);
	}

}
