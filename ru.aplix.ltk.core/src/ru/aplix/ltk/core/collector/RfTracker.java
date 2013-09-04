package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.message.MsgConsumer;


/**
 * RFID tags tracker.
 *
 * <p>It is responsible for maintaining RFID tag presence tracking and
 * reporting of {@link RfTagAppearanceMessage changes in tags appearance}.</p>
 *
 * <p>A tag tracker instance can be passed to {@link RfCollector} constructor.
 * It will be initialized and used by that collector.</p>
 */
public interface RfTracker {

	/**
	 * Initializes the RFID tags tracker.
	 *
	 * <p>This method is called once before the tracking started.</p>
	 *
	 * @param tracking RFID tags tracking, which can be used to report changes
	 * in tag appearance.
	 */
	void initRfTracker(RfTracking tracking);

	/**
	 * Starts RFID tags tracking.
	 *
	 * <p>This is called by collector on the first
	 * {@link RfCollectorHandle#requestTagAppearance(MsgConsumer) RFID tags
	 * appearance subscription}.</p>
	 */
	void startRfTracking();

	/**
	 * Updates the tracking information.
	 *
	 * <p>This method is invoked upon each reception of RFID reader data.</p>
	 *
	 * @param dataMessage RFID reader data message.
	 */
	void rfData(RfDataMessage dataMessage);

	/**
	 * Stops RIFD tags tracking.
	 *
	 * <p>This is called by collector when the last RFID tags appearance
	 * subscription revoked.</p>
	 */
	void stopRfTracking();

}
