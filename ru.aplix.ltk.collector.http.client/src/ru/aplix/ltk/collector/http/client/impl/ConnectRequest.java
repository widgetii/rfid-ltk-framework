package ru.aplix.ltk.collector.http.client.impl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;

import ru.aplix.ltk.collector.http.ClrClientRequest;
import ru.aplix.ltk.collector.http.ClrClientResponse;


public class ConnectRequest extends MessageSender<ClrClientResponse> {

	public ConnectRequest(HttpRfConnection connection) {
		super(connection);
	}

	@Override
	public ClrClientResponse call() throws Exception {
		if (getConnection().isConnected()) {
			return null;// Already connected.
		}

		final ClrClientRequest request = new ClrClientRequest();

		request.setClientURL(getSettings().getClientURL());

		final ClrClientResponse response = put(null, request);
		final UUID clientUUID = response.getClientUUID();

		requireNonNull(
				clientUUID,
				"No client UUID in connection response");

		getConnection().connectionEstablished(clientUUID);

		return response;
	}

	@Override
	protected ClrClientResponse handleEntity(
			HttpEntity entity)
	throws ClientProtocolException, IOException {
		return new ClrClientResponse(parseResponse(entity));
	}

}
