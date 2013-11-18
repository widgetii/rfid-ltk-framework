package ru.aplix.ltk.store.web.receiver;

import ru.aplix.ltk.store.RfTagEvent;


public class RfTagEventBean {

	private final RfReceiverDesc receiver;
	private final long timestamp;
	private final String antennas;
	private final String tag;
	private final boolean appeared;

	public RfTagEventBean(RfReceiverDesc receiver, RfTagEvent event) {
		this.receiver = receiver;
		this.timestamp = event.getTimestamp();
		this.antennas = event.getAntennas().toString();
		this.tag = event.getRfTag().toHexString();
		this.appeared = event.getAppearance().isPresent();
	}

	public final RfReceiverDesc getReceiver() {
		return this.receiver;
	}

	public final long getTimestamp() {
		return this.timestamp;
	}

	public final String getAntennas() {
		return this.antennas;
	}

	public final String getTag() {
		return this.tag;
	}

	public final boolean isAppeared() {
		return this.appeared;
	}

}
