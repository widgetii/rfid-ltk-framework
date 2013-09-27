package ru.aplix.ltk.collector.http.client.impl;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;

import ru.aplix.ltk.collector.http.ClrClientRequest;


public class ConnectRequest extends MessageSender<Boolean> {

	public ConnectRequest(HttpRfConnection connection) {
		super(connection);
	}

	@Override
	public Boolean call() throws Exception {
		if (getConnection().isConnected()) {
			getLogger().warning(getConnection() + " Already connected");
			return null;// Already connected.
		}

		getLogger().info(getConnection() + " Connecting");

		final ClrClientRequest request = new ClrClientRequest();

		request.setClientURL(getSettings().getClientURL());
		request.setLastTagEventId(getConnection().getLastTagEventId());

		return put(request);
	}

	@Override
	protected Boolean handleEntity(
			HttpEntity entity)
	throws ClientProtocolException, IOException {
		getConnection().connectionEstablished();
		return Boolean.TRUE;
	}

}
