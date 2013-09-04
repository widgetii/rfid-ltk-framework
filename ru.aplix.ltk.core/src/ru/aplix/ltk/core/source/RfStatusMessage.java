package ru.aplix.ltk.core.source;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.message.MsgConsumer;


/**
 * A message indicating the status of the RFID source.
 *
 * <p>Such messages can be received e.g. after
 * {@link RfReader#subscribe(MsgConsumer) reader}, or
 * {@link RfCollector#subscribe(MsgConsumer) collector} subscription.</p>
 */
public interface RfStatusMessage {

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
	 * RFID source status.
	 *
	 * @return source status.
	 */
	RfStatus getRfStatus();

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
