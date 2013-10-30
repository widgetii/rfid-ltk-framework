package ru.aplix.ltk.store.web.receiver;

import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;


public class RfTagBean {

	private final RfReceiverBean receiver;
	private final long id;
	private final long timestamp;
	private final String tag;
	private final boolean appeared;

	public RfTagBean(RfReceiverBean receiver, RfTagAppearanceMessage message) {
		this.receiver = receiver;
		this.id = message.getEventId();
		this.timestamp = message.getTimestamp();
		this.tag = message.getRfTag().toHexString();
		this.appeared = message.getAppearance().isPresent();
	}

	public final RfReceiverBean getReceiver() {
		return this.receiver;
	}

	public final long getId() {
		return this.id;
	}

	public final long getTimestamp() {
		return this.timestamp;
	}

	public final String getTag() {
		return this.tag;
	}

	public final boolean isAppeared() {
		return this.appeared;
	}

}
