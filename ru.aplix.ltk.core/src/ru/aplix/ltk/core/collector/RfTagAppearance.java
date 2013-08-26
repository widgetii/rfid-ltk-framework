package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.reader.RfTag;


/**
 * RFID tag appearance within reader's a field of view (FOV).
 */
public enum RfTagAppearance {

	/**
	 * RFID tag appeared within reader's FOV.
	 */
	RF_TAG_APPEARED() {

		@Override
		String toString(RfTag rfTag) {
			return "RfTagAppeared[" + rfTag + ']';
		}

	},

	/**
	 * RFID tag disappeared from reader's FOV.
	 */
	RF_TAG_DISAPPEARED() {

		@Override
		String toString(RfTag rfTag) {
			return "RfTagDisappeared[" + rfTag + ']';
		}

	};

	/**
	 * Whether an RFID tag is present within reader's FOV.
	 *
	 * @return <code>true</code> if so, or <code>false</code> otherwise.
	 */
	public final boolean isPresent() {
		return this == RF_TAG_APPEARED;
	}

	abstract String toString(RfTag rfTag);

}
