package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.message.MsgConsumer;


/**
 * A message indicating the status of RFID reader.
 *
 * <p>To receive such messages a {@link RfReader#subscribe(MsgConsumer)
 * subscription} should be made.<p>
 */
public interface RfReaderStatusMessage {

	/**
	 * RFID reader device identifier.
	 *
	 * <p>This is not necessarily the same as {@link RfReader#getRfPortId()
	 * reader's port identifier}. It is expected that the device identifier
	 * id obtained from the device itself upon successful connection.</p>
	 *
	 * @return string identifier, or <code>null</code> if unknown.
	 */
	String getRfReaderId();

	/**
	 * RFID reader status.
	 *
	 * @return reader status.
	 */
	RfReaderStatus getRfReaderStatus();

	/**
	 * RFID reader error message.
	 *
	 * @return error message, or <code>null</code> if nothing to report.
	 */
	String getErrorMessage();

	/**
	 * A cause of this error.
	 *
	 * @return a throwable caused this error, or <code>null</code> if nothing
	 * to report.
	 */
	Throwable getCause();

}
