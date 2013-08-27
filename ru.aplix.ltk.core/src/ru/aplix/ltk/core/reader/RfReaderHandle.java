package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgServiceHandle;


/**
 * RFID reader service subscription handle.
 *
 * <p>It can be used to subscribe on other types of messages originated from
 * the reader.</p>
 */
public final class RfReaderHandle
		extends MsgServiceHandle<RfReaderHandle, RfReaderStatusMessage> {

	private final RfReader reader;

	RfReaderHandle(
			RfReader reader,
			MsgConsumer<
				? super RfReaderHandle,
				? super RfReaderStatusMessage> consumer) {
		super(reader, consumer);
		this.reader = reader;
	}

	/**
	 * Subscribes the given consumer to receive RFID reader data.
	 *
	 * @param consumer RFID reader data consumer to subscribe.
	 *
	 * @return subscription handle.
	 */
	public final RfDataHandle requestData(
			MsgConsumer<? super RfDataHandle, ? super RfDataMessage> consumer) {
		return addSubscription(
				this.reader.dataSubscriptions().subscribe(consumer));
	}

}
