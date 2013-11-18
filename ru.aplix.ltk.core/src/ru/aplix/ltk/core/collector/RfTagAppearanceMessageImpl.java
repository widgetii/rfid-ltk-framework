package ru.aplix.ltk.core.collector;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static ru.aplix.ltk.core.util.IntSet.EMPTY_INT_SET;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.util.IntSet;


final class RfTagAppearanceMessageImpl implements RfTagAppearanceMessage {

	private final IntSet antennas;
	private final long timestamp;
	private final RfTag rfTag;
	private final RfTagAppearance appearance;
	private final boolean initialEvent;

	RfTagAppearanceMessageImpl(
			IntSet antennas,
			RfTag rfTag,
			RfTagAppearance appearance,
			boolean initialEvent) {
		requireNonNull(rfTag, "RFID tag not specified");
		this.timestamp = currentTimeMillis();
		this.antennas = antennas != null ? antennas : EMPTY_INT_SET;
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
	public IntSet getAntennas() {
		return this.antennas;
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
