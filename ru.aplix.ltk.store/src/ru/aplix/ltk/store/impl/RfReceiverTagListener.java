package ru.aplix.ltk.store.impl;

import java.util.concurrent.atomic.AtomicLong;

import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.store.impl.persist.RfTagEventData;


final class RfReceiverTagListener
		implements MsgConsumer<RfTagAppearanceHandle, RfTagAppearanceMessage> {

	private final RfReceiverImpl<?> receiver;
	private final AtomicLong eventId = new AtomicLong();

	RfReceiverTagListener(RfReceiverImpl<?> receiver) {
		this.receiver = receiver;
	}

	public long retrieveLastEventId() {

		final long lastEventId =
				this.receiver.getRfStore().lastEventId(this.receiver);

		this.eventId.set(lastEventId);

		return lastEventId;
	}

	@Override
	public void consumerSubscribed(RfTagAppearanceHandle handle) {
	}

	@Override
	public void messageReceived(RfTagAppearanceMessage message) {

		final RfTagEventData event =
				new RfTagEventData(this.receiver, message);

		this.receiver.getRfStore().saveEvent(event);
	}

	@Override
	public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
	}

}
