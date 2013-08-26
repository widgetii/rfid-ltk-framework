package ru.aplix.ltk.core.collector;

import static java.util.Objects.requireNonNull;
import ru.aplix.ltk.core.reader.RfTag;


/**
 * A change in RFID tag appearance.
 */
public final class RfTagAppearanceMessage {

	private final RfTag rfTag;
	private final RfTagAppearance appearance;

	RfTagAppearanceMessage(RfTag rfTag, RfTagAppearance appearance) {
		requireNonNull(rfTag, "RFID tag not specified");
		this.rfTag = rfTag;
		this.appearance = appearance;
	}

	/**
	 * RFID tag, which appearance has changed.
	 *
	 * @return RFID tag instance.
	 */
	public final RfTag getRfTag() {
		return this.rfTag;
	}

	/**
	 * RFID tag appearance.
	 *
	 * @return appearance status.
	 */
	public final RfTagAppearance getAppearance() {
		return this.appearance;
	}

	@Override
	public String toString() {
		if (this.appearance == null) {
			return super.toString();
		}
		return this.appearance.toString(this.rfTag);
	}

}
