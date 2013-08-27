package ru.aplix.ltk.core.test.reader;

import ru.aplix.ltk.core.reader.RfDataMessage;
import ru.aplix.ltk.core.reader.RfTag;


public class TestDataMessage implements RfDataMessage {

	private final long timestamp;
	private final TestTag tag;
	private final int transactionId;
	private final boolean transactionEnd;

	public TestDataMessage(
			long timestamp,
			TestTag tag,
			int transactionId,
			boolean transactionEnd) {
		this.timestamp = timestamp;
		this.tag = tag;
		this.transactionId = transactionId;
		this.transactionEnd = transactionEnd;
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
		return this.transactionId;
	}

	@Override
	public boolean isRfTransactionEnd() {
		return this.transactionEnd;
	}

}
