package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.source.RfTag;


public class DummyTag implements RfTag {

	private final long tag;

	DummyTag() {
		this.tag = 0x100000000L + (long) (Math.random() * 0xffffffffL);
	}

	@Override
	public String toString() {
		return "0x" + Long.toHexString(this.tag);
	}

}
