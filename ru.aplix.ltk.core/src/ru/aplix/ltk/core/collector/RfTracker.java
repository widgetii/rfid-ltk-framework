package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.reader.RfReadMessage;


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
	 * Updates the tracking information.
	 *
	 * <p>This method is invoked upon each reception of RFID read message.</p>
	 *
	 * @param readMessage RFID read operation message.
	 */
	void rfRead(RfReadMessage readMessage);

}
