package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.source.RfTag;


/**
 * A change in RFID tag appearance.
 */
public interface RfTagAppearanceMessage {

	/**
	 * RFID tag, which appearance has changed.
	 *
	 * @return RFID tag instance.
	 */
	RfTag getRfTag();

	/**
	 * RFID tag appearance.
	 *
	 * @return appearance status.
	 */
	RfTagAppearance getAppearance();

}
