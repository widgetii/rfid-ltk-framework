package ru.aplix.ltk.core.source;

import ru.aplix.ltk.core.collector.RfCollector;


/**
 * An RFID status updater.
 *
 * <p>An implementation of this class is provided to
 * {@link RfSource#requestRfStatus(RfStatusUpdater)} by its consumer, e.g by
 * {@link RfCollector}. It can be used to report RFID events.</p>
 */
public interface RfStatusUpdater {

	/**
	 * Reports reader status update.
	 *
	 * @param status status to report.
	 */
	void updateStatus(RfStatusMessage status);

}
