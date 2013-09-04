package ru.aplix.ltk.core.source;


/**
 * RFID data receiver.
 *
 * <p>An instance of this class is passed to {@link RfSource#requestRfData(
 * RfDataReceiver) RFID data source}, so that the latter can send the data.</p>
 */
public interface RfDataReceiver {

	/**
	 * Sends RFID data.
	 *
	 * @param data RFID data to send.
	 */
	void sendRfData(RfDataMessage data);

}
