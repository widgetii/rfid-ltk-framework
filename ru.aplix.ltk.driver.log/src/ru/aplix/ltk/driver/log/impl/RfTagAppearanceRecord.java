package ru.aplix.ltk.driver.log.impl;

import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;
import static ru.aplix.ltk.core.util.CRC8.calcCRC8;
import static ru.aplix.ltk.core.util.IntSet.EMPTY_INT_SET;
import static ru.aplix.ltk.core.util.IntSet.intSetByMask;

import java.nio.ByteBuffer;

import ru.aplix.ltk.core.collector.RfTagAppearance;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.util.IntSet;


final class RfTagAppearanceRecord implements RfTagAppearanceMessage {

	private static final byte PRESENCE_FLAG = 1;
	private static final byte INITIAL_EVENT_FLAG = 2;
	/**
	 * If set, the tag length is embedded into flags as four upper bits, and
	 * antenna identifiers mask is present as two bytes after flags.
	 * Otherwise, the length is one byte after flags, and antenna identifiers
	 * are absent (legacy mode).
	 */
	private static final byte EMBEDDED_LENGTH = 4;

	private final long eventId;
	private final long timestamp;
	private final IntSet antennas;
	private final RfTag rfTag;
	private final RfTagAppearance appearance;
	private final boolean initialEvent;

	RfTagAppearanceRecord(long eventId, RfTagAppearanceMessage message) {
		this.eventId = eventId;
		this.timestamp = message.getTimestamp();
		this.antennas = message.getAntennas();
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

		final int length;

		if ((flags & EMBEDDED_LENGTH) != 0) {
			length = 0xff & (flags >>> 4);
			this.antennas = intSetByMask(data.getShort() & 0xFFFF);
		} else {
			this.antennas = EMPTY_INT_SET;
			length = data.get();
		}
		this.rfTag = readTag(data, length);
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

	public void write(ByteBuffer out) {
		out.putLong(getEventId());
		out.putLong(getTimestamp());

		final byte[] tagData = getRfTag().getData();

		out.put(flags(tagData.length));
		out.putShort((short) getAntennas().toMask());
		out.put(tagData);
		writeCRC(out);
	}

	private byte flags(int length) {
		if (length > 12) {
			throw new IllegalStateException("Tag is too long: " + getRfTag());
		}

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
		flags |= EMBEDDED_LENGTH;
		flags |= length << 4;

		return flags;
	}

	private static RfTagAppearance appearanceByFlags(byte flags) {
		if ((flags & PRESENCE_FLAG) != 0) {
			return RF_TAG_APPEARED;
		}
		return RF_TAG_DISAPPEARED;
	}

	private static RfTag readTag(ByteBuffer data, int length) {
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