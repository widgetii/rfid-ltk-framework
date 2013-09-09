package ru.aplix.ltk.collector.http.client.impl;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;


public class DisconnectRequest extends MessageSender<Void> {

	public DisconnectRequest(HttpRfConnection connection) {
		super(connection);
	}

	@Override
	public Void call() throws Exception {
		if (!getConnection().isConnected()) {
			return null;
		}
		return delete(null);
	}

	@Override
	protected Void handleEntity(
			HttpEntity entity)
	throws ClientProtocolException, IOException {
		return null;
	}

}
