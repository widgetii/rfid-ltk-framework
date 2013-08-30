package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.reader.RfDataMessage;
import ru.aplix.ltk.core.reader.RfTag;


public class DummyRfDataMessage implements RfDataMessage {

	private final long timestamp;
	private final DummyTag tag;
	private final int transactionId;
	private final boolean transactionEnd;

	public DummyRfDataMessage(
			DummyTag tag,
			int transactionId,
			boolean transactionEnd) {
		this.timestamp = System.currentTimeMillis();
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
