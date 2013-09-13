package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.message.MsgConsumer;



final class RfStoreTagListener
		implements MsgConsumer<RfTagAppearanceHandle, RfTagAppearanceMessage> {

	@Override
	public void consumerSubscribed(RfTagAppearanceHandle handle) {
	}

	@Override
	public void messageReceived(RfTagAppearanceMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
	}

}
