package ru.aplix.ltk.core.collector;

import static java.util.Objects.requireNonNull;
import ru.aplix.ltk.core.source.RfTag;


final class RfTagAppearanceMessageImpl implements RfTagAppearanceMessage {

	private final long timestamp;
	private final RfTag rfTag;
	private final RfTagAppearance appearance;
	private final boolean initialEvent;

	RfTagAppearanceMessageImpl(
			RfTag rfTag,
			RfTagAppearance appearance,
			boolean initialEvent) {
		requireNonNull(rfTag, "RFID tag not specified");
		this.timestamp = System.currentTimeMillis();
		this.rfTag = rfTag;
		this.appearance = appearance;
		this.initialEvent = initialEvent;
	}

	@Override
	public final long getEventId() {
		return 0;
	}

	@Override
	public final boolean isInitialEvent() {
		return this.initialEvent;
	}

	@Override
	public final long getTimestamp() {
		return this.timestamp;
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
