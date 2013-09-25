package ru.aplix.ltk.core.collector;

import static java.util.Objects.requireNonNull;
import ru.aplix.ltk.core.source.RfTag;


final class RfTagAppearanceMessageImpl implements RfTagAppearanceMessage {

	private final RfTag rfTag;
	private final RfTagAppearance appearance;

	RfTagAppearanceMessageImpl(RfTag rfTag, RfTagAppearance appearance) {
		requireNonNull(rfTag, "RFID tag not specified");
		this.rfTag = rfTag;
		this.appearance = appearance;
	}

	@Override
	public long getEventId() {
		return 0;
	}

	@Override
	public final RfTag getRfTag() {
		return this.rfTag;
	}

	@Override
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
