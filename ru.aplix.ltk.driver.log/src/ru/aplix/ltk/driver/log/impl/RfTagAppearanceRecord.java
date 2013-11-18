package ru.aplix.ltk.driver.log.impl;

import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;
import static ru.aplix.ltk.core.util.CRC8.calcCRC8;
import static ru.aplix.ltk.core.util.IntSet.EMPTY_INT_SET;

import java.nio.ByteBuffer;

import ru.aplix.ltk.core.collector.RfTagAppearance;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.util.IntSet;


final class RfTagAppearanceRecord implements RfTagAppearanceMessage {

	private static final byte PRESENCE_FLAG = 1;
	private static final byte INITIAL_EVENT_FLAG = 2;

	private final long eventId;
	private final long timestamp;
	private final RfTag rfTag;
	private final RfTagAppearance appearance;
	private final boolean initialEvent;

	RfTagAppearanceRecord(long eventId, RfTagAppearanceMessage message) {
		this.eventId = eventId;
		this.timestamp = message.getTimestamp();
		this.rfTag = message.getRfTag();
		this.appearance = message.getAppearance();
		this.initialEvent = message.isInitialEvent();
	}

	RfTagAppearanceRecord(ByteBuffer data) {
		this.eventId = data.getLong();
		this.timestamp = data.getLong();

		final byte flags = data.get();

		this.appearance = appearanceByFlags(flags);
		this.initialEvent = (flags & INITIAL_EVENT_FLAG) != 0;
		this.rfTag = readTag(data);
		checkCRC(data);
	}

	@Override
	public final long getEventId() {
		return this.eventId;
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
		// TODO Implement antenna identifiers logging.
		return EMPTY_INT_SET;
	}

	@Override
	public final RfTag getRfTag() {
		return this.rfTag;
	}

	@Override
	public final RfTagAppearance getAppearance() {
		return this.appearance;
	}

	public void write(ByteBuffer out) {
		out.putLong(getEventId());
		out.putLong(getTimestamp());
		out.put(flags());
		writeTag(out);
		writeCRC(out);
	}

	private byte flags() {

		byte flags = 0;

		switch (getAppearance()) {
		case RF_TAG_APPEARED:
			flags |= PRESENCE_FLAG;
			break;
		case RF_TAG_DISAPPEARED:
			break;
		}

		if (isInitialEvent()) {
			flags |= INITIAL_EVENT_FLAG;
		}

		return flags;
	}

	private static RfTagAppearance appearanceByFlags(byte flags) {
		if ((flags & PRESENCE_FLAG) != 0) {
			return RF_TAG_APPEARED;
		}
		return RF_TAG_DISAPPEARED;
	}

	private void writeTag(ByteBuffer out) {

		final byte[] tagData = getRfTag().getData();

		if (tagData.length > 12) {
			throw new IllegalStateException("Tag is too long: " + getRfTag());
		}
		out.put((byte) tagData.length);
		out.put(tagData);
	}

	private static RfTag readTag(ByteBuffer data) {

		final int length = data.get();

		if (length > 12) {
			throw new IllegalStateException("Tag data is too long: " + length);
		}

		final byte[] tagData = new byte[length];

		data.get(tagData);

		return new RfTag(tagData);
	}

	private void writeCRC(ByteBuffer out) {
		out.flip();

		final byte crc = calcCRC8(out);

		out.limit(out.capacity());
		out.put(crc);
	}

	private static void checkCRC(ByteBuffer data) {

		final int position = data.position();
		final byte expected = data.get();

		data.limit(position);
		data.rewind();

		final byte calculated = calcCRC8(data);

		if (expected != calculated) {
			throw new IllegalStateException("CRC failed");
		}
	}

}