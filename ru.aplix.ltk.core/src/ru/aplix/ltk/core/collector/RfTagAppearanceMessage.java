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
	 * @return positive identifier, or non-positive value if not supported by
	 * provider.
	 */
	long getEventId();

	/**
	 * Whether this event is initial.
	 *
	 * <p>An initial event is occurred once, after collector startup.</p>
	 *
	 * <p>Some tags may disappear while the collector is offline. This flag may
	 * be used by the client to make disappear of all tags it still considers
	 * visible.</p>
	 *
	 * @return <code>true</code> if this event is initial, or <code>false</code>
	 * otherwise.
	 */
	boolean isInitialEvent();

	/**
	 * Event time.
	 *
	 * @return UNIX time of event.
	 */
	long getTimestamp();

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
