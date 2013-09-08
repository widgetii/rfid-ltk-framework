package ru.aplix.ltk.core.test.reader;

import ru.aplix.ltk.core.source.RfTag;


public final class TestTags {

	public static RfTag randomTag() {
		return testTag(0x100000000L + (long) (Math.random() * 0xffffffffL));
	}

	public static RfTag testTag(long value) {
		return new RfTag(new byte[] {
			(byte) (value & 0xff),
			(byte) ((value >>> 8) & 0xff),
			(byte) ((value >>> 16) & 0xff),
			(byte) ((value >>> 24) & 0xff),
			(byte) ((value >>> 32) & 0xff),
			(byte) ((value >>> 40) & 0xff),
			(byte) ((value >>> 48) & 0xff),
			(byte) ((value >>> 56) & 0xff),
		});
	}

}
