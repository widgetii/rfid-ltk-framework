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
		getLogger().info(getConnection() + " Closing connection");
		return delete();
	}

	@Override
	protected Void handleEntity(
			HttpEntity entity)
	throws ClientProtocolException, IOException {
		return null;
	}

}
