package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.message.MsgConsumer;


/**
 * A message about RFID tag read by reader device.
 *
 * <p>To receive such messages a {@link RfReaderHandle#requestTags(MsgConsumer)
 * subscription} should be made.<p>
 */
public interface RfTagMessage {

	/**
	 * RFID tag read by reader device.
	 *
	 * @return RFID tag.
	 */
	RfTag getRfTag();

}
