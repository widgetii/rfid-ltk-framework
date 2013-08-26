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
	 * Subscribes the given consumer to receive RFID read messages.
	 *
	 * @param consumer RFID read operation results consumer to subscribe.
	 *
	 * @return RFID read operations subscription handle.
	 */
	public final RfReadHandle read(
			MsgConsumer<? super RfReadHandle, ? super RfReadMessage> consumer) {
		return addSubscription(
				this.reader.tagSubscriptions().subscribe(consumer));
	}

}
