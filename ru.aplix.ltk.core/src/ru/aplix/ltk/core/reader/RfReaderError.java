package ru.aplix.ltk.core.reader;

import static ru.aplix.ltk.core.reader.RfReaderStatus.RF_READER_ERROR;


/**
 * RFID reader error message.
 */
public class RfReaderError implements RfReaderStatusMessage {

	private final String readerId;
	private final String errorMessage;
	private final Throwable cause;

	/**
	 * Constructs an error message.
	 *
	 * @param readerId reader device identifier.
	 * @param errorMessage error message to report, or <code>null</code>.
	 */
	public RfReaderError(String readerId, String errorMessage) {
		this.readerId = readerId;
		this.errorMessage = errorMessage;
		this.cause = null;
	}

	/**
	 * Constructs an error message with the given cause.
	 *
	 * @param readerId reader device identifier.
	 * @param cause a throwable caused this error, or <code>null</code>.
	 */
	public RfReaderError(String readerId, Throwable cause) {
		this.readerId = readerId;
		if (cause != null) {
			this.errorMessage = cause.getMessage();
			this.cause = cause;
		} else {
			this.errorMessage = null;
			this.cause = null;
		}
	}

	@Override
	public String getRfReaderId() {
		return this.readerId;
	}

	@Override
	public RfReaderStatus getRfReaderStatus() {
		return RF_READER_ERROR;
	}

	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public Throwable getCause() {
		return this.cause;
	}

	@Override
	public String toString() {
		if (this.errorMessage == null) {
			return super.toString();
		}
		return "RfReaderError(" + this.readerId
				+ ")[" + this.errorMessage + ']';
	}

}
