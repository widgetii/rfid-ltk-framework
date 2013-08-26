package ru.aplix.ltk.core.reader;

import static ru.aplix.ltk.core.reader.RfReaderStatus.RF_READER_CONNECTED;


/**
 * RFID reader connected status message.
 */
public class RfReaderConnected implements RfReaderStatusMessage {

	private final String readerId;

	/**
	 * Constructs message.
	 *
	 * @param readerId reader device identifier.
	 */
	public RfReaderConnected(String readerId) {
		this.readerId = readerId;
	}

	@Override
	public String getRfReaderId() {
		return this.readerId;
	}

	@Override
	public RfReaderStatus getRfReaderStatus() {
		return RF_READER_CONNECTED;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public Throwable getCause() {
		return null;
	}

	@Override
	public String toString() {
		return "RfReaderConnected[" + this.readerId + ']';
	}

}
