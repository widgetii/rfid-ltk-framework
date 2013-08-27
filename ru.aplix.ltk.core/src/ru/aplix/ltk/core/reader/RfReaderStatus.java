package ru.aplix.ltk.core.reader;


/**
 * RFID reader status.
 */
public enum RfReaderStatus {

	/**
	 * RFID reader is connected and functioning properly.
	 */
	RF_READER_CONNECTED,

	/**
	 * RFID reader error.
	 */
	RF_READER_ERROR;

	/**
	 * Whether RFID error happened.
	 *
	 * @return <code>true</code> if this status is a reader error status,
	 * or <code>false</code> otherwise.
	 */
	public boolean isError() {
		return this == RF_READER_ERROR;
	}

}
