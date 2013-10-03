package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.store.impl.persist.RfTagEventData;


final class RfReceiverTagListener
		implements MsgConsumer<RfTagAppearanceHandle, RfTagAppearanceMessage> {

	private final RfReceiverImpl<?> receiver;

	RfReceiverTagListener(RfReceiverImpl<?> receiver) {
		this.receiver = receiver;
	}

	@Override
	public void consumerSubscribed(RfTagAppearanceHandle handle) {
	}

	@Override
	public void messageReceived(RfTagAppearanceMessage message) {

		final RfTagEventData tagData =
				new RfTagEventData(this.receiver, message);

		this.receiver.getRfStore().saveEvent(tagData);
	}

	@Override
	public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
	}

}
