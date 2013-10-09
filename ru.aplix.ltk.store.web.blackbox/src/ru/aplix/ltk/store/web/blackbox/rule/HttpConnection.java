package ru.aplix.ltk.store.web.blackbox.rule;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.junit.rules.ExternalResource;


public class HttpConnection extends ExternalResource {

	private HttpClient httpClient;

	public final HttpClient getHttpClient() {
		return this.httpClient;
	}

	public HttpPost post(String path) {
		return new HttpPost("http://localhost:28080/blackbox/" + path);
	}

	@Override
	protected void before() throws Throwable {
		this.httpClient = new DefaultHttpClient(new SingleClientConnManager());
	}

	@Override
	protected void after() {
		this.httpClient.getConnectionManager().shutdown();
	}

}
