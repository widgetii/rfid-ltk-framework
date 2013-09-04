package ru.aplix.ltk.core.source;

import static ru.aplix.ltk.core.source.RfStatus.RF_ERROR;


/**
 * RFID error message.
 */
public class RfError implements RfStatusMessage {

	private final String readerId;
	private final String errorMessage;
	private final Throwable cause;

	/**
	 * Constructs an error message.
	 *
	 * @param readerId reader device identifier.
	 * @param errorMessage error message to report, or <code>null</code>.
	 */
	public RfError(String readerId, String errorMessage) {
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
	public RfError(String readerId, Throwable cause) {
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
	public RfStatus getRfStatus() {
		return RF_ERROR;
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
