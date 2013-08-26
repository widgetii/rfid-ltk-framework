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
	 * RFID device identifier.
	 *
	 * @return string identifier.
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
