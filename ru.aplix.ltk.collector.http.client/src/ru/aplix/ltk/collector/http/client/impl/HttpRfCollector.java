package ru.aplix.ltk.collector.http.client.impl;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.message.MsgConsumer;


final class HttpRfCollector extends RfCollector {

	private final HttpRfConnection connection;

	public HttpRfCollector(HttpRfConnection connection) {
		super(connection, connection);
		this.connection = connection;
	}

	@Override
	protected MsgConsumer<
			? super RfTagAppearanceHandle,
			? super RfTagAppearanceMessage> requestTagAppearance(
					MsgConsumer<
							? super RfTagAppearanceHandle,
							? super RfTagAppearanceMessage> consumer,
					long lastEventId) {
		if (this.connection.isConnected()) {
			throw new IllegalStateException(
					"Connection already opened: " + this.connection);
		}
		this.connection.setLastTagEventId(lastEventId);
		return consumer;
	}

}
