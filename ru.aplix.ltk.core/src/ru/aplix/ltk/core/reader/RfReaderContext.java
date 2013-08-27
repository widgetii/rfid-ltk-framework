package ru.aplix.ltk.core.reader;


/**
 * RFID reader context.
 *
 * <p>An instance of this class is passed to {@link RfReaderDriver#initRfReader(
 * RfReaderContext) reader}, and can be used by this reader to report reader
 * events. The messages sent though this context will be reported to all
 * subscribed consumers.</p>
 */
public final class RfReaderContext {

	private final RfReader reader;

	RfReaderContext(RfReader reader) {
		this.reader = reader;
	}

	/**
	 * Reports an RFID reader status update.
	 *
	 * @param statusMessage status update to report.
	 */
	public void updateStatus(RfReaderStatusMessage statusMessage) {
		this.reader.readerSubscriptions().sendMessage(statusMessage);
	}

	/**
	 * Sends an RFID read result.
	 *
	 * @param readMessage read result to report.
	 */
	public void sendRead(RfReadMessage readMessage) {
		this.reader.tagSubscriptions().sendMessage(readMessage);
	}

}
