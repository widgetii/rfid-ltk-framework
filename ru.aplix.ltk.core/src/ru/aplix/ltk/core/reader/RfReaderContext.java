package ru.aplix.ltk.core.reader;

import ru.aplix.ltk.core.source.*;


/**
 * RFID reader context.
 *
 * <p>An instance of this class is passed to {@link RfReaderDriver#initRfReader(
 * RfReaderContext) reader}, and can be used by this reader to report reader
 * events. The messages sent though this context will be reported to all
 * subscribed consumers.</p>
 */
public final class RfReaderContext implements RfStatusUpdater, RfDataReceiver {

	private final RfReader reader;

	RfReaderContext(RfReader reader) {
		this.reader = reader;
	}

	/**
	 * Reports an RFID reader status update.
	 *
	 * @param statusMessage status update to report.
	 */
	@Override
	public void updateStatus(RfStatusMessage statusMessage) {
		this.reader.readerSubscriptions().sendMessage(statusMessage);
	}

	/**
	 * Sends RFID reader data.
	 *
	 * @param dataMessage reader data to send.
	 */
	@Override
	public void sendRfData(RfDataMessage dataMessage) {
		this.reader.dataSubscriptions().sendMessage(dataMessage);
	}

}
