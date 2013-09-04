package ru.aplix.ltk.core.source;

import static ru.aplix.ltk.core.source.RfStatus.RF_READY;


/**
 * RFID source connection status message.
 */
public class RfConnected implements RfStatusMessage {

	private final String readerId;

	/**
	 * Constructs message.
	 *
	 * @param readerId reader device identifier.
	 */
	public RfConnected(String readerId) {
		this.readerId = readerId;
	}

	@Override
	public String getRfReaderId() {
		return this.readerId;
	}

	@Override
	public RfStatus getRfStatus() {
		return RF_READY;
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
