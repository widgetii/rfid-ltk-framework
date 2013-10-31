package ru.aplix.ltk.store.web.receiver;

import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfTagQuery;


public class RfTagQueryBean {

	private static final int DEFAULT_PAGE_SIZE = 50;

	private int receiver;
	private String tag;
	private long since;
	private int offset;
	private int limit = DEFAULT_PAGE_SIZE;

	public int getReceiver() {
		return this.receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		if (tag == null) {
			this.tag = null;
			return;
		}

		final String trimmed = tag.trim();

		this.tag = trimmed.isEmpty() ? null : trimmed;
	}

	public long getSince() {
		return this.since;
	}

	public void setSince(long since) {
		this.since = since;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public RfTagQuery buildQuery(RfStore store) {

		final RfTagQuery query = createQuery(store);

		return query.setTag(getTag())
		.setSince(getSince())
		.setOffset(getOffset())
		.setLimit(getLimit());
	}

	private RfTagQuery createQuery(RfStore store) {
		if (getReceiver() > 0) {
			return store.rfReceiverById(getReceiver()).tagQuery();
		}
		return store.tagQuery();
	}

}
