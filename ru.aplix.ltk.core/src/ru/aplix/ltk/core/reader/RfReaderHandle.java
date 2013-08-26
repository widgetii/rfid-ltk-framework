package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgServiceHandle;


/**
 * RFID reader service subscription handle.
 *
 * <p>It can be used to subscribe on other types of messages from the reader.
 * </p>
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
	 * Subscribes the given consumer to receive RFID tag messages.
	 *
	 * @param consumer RFID tags consumer to subscribe.
	 *
	 * @return RFID tags subscription handle.
	 */
	public final RfTagHandle requestTags(
			MsgConsumer<? super RfTagHandle, ? super RfTagMessage> consumer) {
		return this.reader.tagSubscriptions().subscribe(consumer);
	}

}
