package ru.aplix.ltk.core.source;


/**
 * RFID source status.
 */
public enum RfStatus {

	/**
	 * RFID source is ready. E.g. a connection to RFID reader is established,
	 * and is functioning properly.
	 */
	RF_READY,

	/**
	 * Error occurred in RFID source.
	 */
	RF_ERROR;

	/**
	 * Whether RFID error happened.
	 *
	 * @return <code>true</code> if this status is an error one,
	 * or <code>false</code> otherwise.
	 */
	public boolean isError() {
		return this == RF_ERROR;
	}

}
