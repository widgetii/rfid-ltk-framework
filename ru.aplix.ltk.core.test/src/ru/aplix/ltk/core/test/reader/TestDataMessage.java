package ru.aplix.ltk.core.test.reader;

import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfTag;


public class TestDataMessage implements RfDataMessage {

	private final long timestamp;
	private final RfTag tag;
	private final int transactionId;
	private final boolean transactionEnd;

	public TestDataMessage(
			long timestamp,
			RfTag tag,
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
	public int getAntennaId() {
		return 0;
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
