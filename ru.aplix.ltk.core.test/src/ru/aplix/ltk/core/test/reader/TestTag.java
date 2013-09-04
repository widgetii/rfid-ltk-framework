package ru.aplix.ltk.core.test.reader;

import ru.aplix.ltk.core.source.RfTag;


public class TestTag implements RfTag {

	private final long tag;

	public TestTag() {
		this.tag = 0x100000000L + (long) (Math.random() * 0xffffffffL);
	}

	public TestTag(long tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "0x" + Long.toHexString(this.tag);
	}

}
