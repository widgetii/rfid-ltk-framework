package ru.aplix.ltk.driver.ctg.impl;

import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.types.UnsignedLong_DATETIME;

import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfTag;


public class LLRPDataMessage implements RfDataMessage {

	private final long timestamp;
	private final LLRPTag tag;

	public LLRPDataMessage(TagReportData tag) {

		final UnsignedLong_DATETIME lastSeenTimestamp =
				tag.getLastSeenTimestampUTC().getMicroseconds();

		this.timestamp = lastSeenTimestamp.toLong() / 1000L;
		this.tag = new LLRPTag(tag.getEPCParameter());
	}

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public RfTag getRfTag() {
		return this.tag;
	}

	@Override
	public int getRfTransactionId() {
		return 0;
	}

	@Override
	public boolean isRfTransactionEnd() {
		return false;
	}

}
