package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.source.RfTag;


/**
 * A change in RFID tag appearance.
 */
public interface RfTagAppearanceMessage {

	/**
	 * Event identifier.
	 *
	 * <p>If supported by provider, event identifiers are increasing numbers.
	 * Not every provider has to support them though.</p>
	 *
	 * <p>An event identifiers can be used to request events happened before.
	 * For that a range of event identifiers can be used.</p>
	 *
	 * @return identifier.
	 */
	long getEventId();

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
