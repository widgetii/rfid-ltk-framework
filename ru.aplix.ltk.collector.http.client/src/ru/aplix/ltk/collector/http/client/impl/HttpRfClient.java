package ru.aplix.ltk.collector.http.client.impl;

import static java.util.Collections.synchronizedMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;


public class HttpRfClient {

	private final HttpRfProvider provider;
	private final HttpClient httpClient;
	private final Map<UUID, HttpRfConnection> connections =
			synchronizedMap(new HashMap<UUID, HttpRfConnection>());

	public HttpRfClient(HttpRfProvider provider) {

		this.provider = provider;
		final ThreadSafeClientConnManager conman =
				new ThreadSafeClientConnManager();

		this.httpClient = new DefaultHttpClient(conman);
	}

	public final HttpRfProvider getProvider() {
		return this.provider;
	}

	public final HttpClient httpClient() {
		return this.httpClient;
	}

	public HttpRfConnection connect(HttpRfSettings settings) {
		return new HttpRfConnection(this, settings);
	}

	public HttpRfConnection getConnection(UUID clientUUID) {

		final HttpRfConnection connection = this.connections.get(clientUUID);

		if (connection == null) {
			throw new IllegalArgumentException(
					"No such client here: " + clientUUID);
		}

		return connection;
	}

	public void shutdown() {
		synchronized (this.connections) {
			for (HttpRfConnection connection : this.connections.values()) {
				connection.shutdown();
			}
		}
		this.httpClient.getConnectionManager().shutdown();
	}

	void add(HttpRfConnection connection) {
		this.connections.put(connection.getClientUUID(), connection);
	}

	void remove(HttpRfConnection connection) {
		this.connections.remove(connection.getClientUUID());
	}

}
