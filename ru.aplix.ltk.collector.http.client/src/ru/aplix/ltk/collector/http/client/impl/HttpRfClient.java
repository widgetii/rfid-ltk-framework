package ru.aplix.ltk.collector.http.client.impl;

import static java.util.Collections.synchronizedMap;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;


public class HttpRfClient {

	private final HttpClient httpClient;
	private final Map<String, HttpRfConnection> connections =
			synchronizedMap(new HashMap<String, HttpRfConnection>());

	public HttpRfClient() {

		final ThreadSafeClientConnManager conman =
				new ThreadSafeClientConnManager();

		this.httpClient = new DefaultHttpClient(conman);
	}

	public final HttpClient httpClient() {
		return this.httpClient;
	}

	public HttpRfConnection connect(HttpRfSettings settings) {
		return new HttpRfConnection(this, settings);
	}

	public HttpRfConnection getConnection(String clientPath) {
		return this.connections.get(clientPath);
	}

	public void shutdown() {
		this.httpClient.getConnectionManager().shutdown();
	}

	void connected(HttpRfConnection connection) {
		this.connections.put(connection.getClientPath(), connection);
	}

	void disconnected(HttpRfConnection connection) {
		this.connections.remove(connection.getClientPath());
	}

}
