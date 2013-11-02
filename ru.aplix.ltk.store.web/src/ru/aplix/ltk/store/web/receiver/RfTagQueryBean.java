package ru.aplix.ltk.store.web.receiver;

import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfTagQuery;


public class RfTagQueryBean {

	private static final int DEFAULT_PAGE_SIZE = 50;

	private int receiver;
	private String tag;
	private long since;
	private int page = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;

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

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page < 1 ? 1 : page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
	}

	public RfTagQuery buildQuery(RfStore store) {
		return store.tagQuery()
				.setReceiverId(getReceiver())
				.setTag(getTag())
				.setSince(getSince())
				.setOffset((getPage() - 1) * getPageSize())
				.setLimit(getPageSize());
	}

}
