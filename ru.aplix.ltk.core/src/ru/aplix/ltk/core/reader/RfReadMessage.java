package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.message.MsgConsumer;


/**
 * A message with RFID read operation results.
 *
 * <p>To receive such messages a {@link RfReaderHandle#read(MsgConsumer)
 * subscription} should be made.<p>
 *
 * <p>This message is sent when an RFID tag read. In addition, it can indicate
 * a start and stop of the tags reading transactions, if supported.</p>
 */
public interface RfReadMessage {

	/**
	 * RFID tag read by reader device.
	 *
	 * <p>This method can return <code>null</code>, e.g. to indicate
	 * transaction end.<p>
	 *
	 * @return RFID tag, or <code>null</code> if unknown.
	 */
	RfTag getRfTag();

	/**
	 * Returns an RFID reading transaction identifier.
	 *
	 * <p>This can be the same value if reading transactions not supported by
	 * the reader. If this value changes, then it is assumed that the previous
	 * transaction stopped.</p>
	 *
	 * @return transaction identifier.
	 */
	int getRfTransactionId();

	/**
	 * Indicates the RFID reading transaction ended.
	 *
	 * @return <code>true</code> if all RFID tags read during the reading
	 * transaction, or <code>false</code> otherwise.
	 */
	boolean isRfTransactionEnd();

}
