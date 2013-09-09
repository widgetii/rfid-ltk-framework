package ru.aplix.ltk.collector.http.client.impl;

import static org.apache.http.util.EntityUtils.getContentCharSet;
import static ru.aplix.ltk.core.util.Parameters.UTF_8;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.EntityUtils;

import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public abstract class MessageSender<T>
		implements Callable<T>, ResponseHandler<T> {

	private final HttpRfConnection connection;
	private volatile Future<T> future;

	public MessageSender(HttpRfConnection connection) {
		this.connection = connection;
	}

	public final HttpRfConnection getConnection() {
		return this.connection;
	}

	public final HttpRfSettings getSettings() {
		return getConnection().getSettings();
	}

	public final HttpRfClient getClient() {
		return getConnection().getClient();
	}

	public final HttpClient httpClient() {
		return getClient().httpClient();
	}

	public void send() {

		final ScheduledExecutorService executor =
				getConnection().getExecutor();

		if (executor != null) {
			this.future = executor.submit(this);
		}
	}

	public void cancel() {

		final Future<T> future = this.future;

		if (future != null) {
			this.future = null;
			future.cancel(true);
		}
	}

	@Override
	public T handleResponse(
			HttpResponse response)
	throws ClientProtocolException, IOException {
		if (this.future == null) {
			return null;// Cancelled.
		}

		final StatusLine statusLine = response.getStatusLine();
		final HttpEntity entity = response.getEntity();

		if (statusLine.getStatusCode() >= 300) {

			EntityUtils.consume(entity);

			throw new HttpResponseException(
					statusLine.getStatusCode(),
					statusLine.getReasonPhrase());
		}

		return handleEntity(entity);
	}

	protected abstract T handleEntity(
			HttpEntity entity)
	throws ClientProtocolException, IOException;

	protected T get(String path) {

		final URL requestURL = requestURL(path);

		if (requestURL == null) {
			return null;
		}
		try {
			return sendRequest(new HttpGet(requestURL.toURI()));
		} catch (Throwable e) {
			requestFailed(requestURL, e);
		}

		return null;
	}

	protected T delete(String path) {

		final URL requestURL = requestURL(path);

		if (requestURL == null) {
			return null;
		}
		try {
			return sendRequest(new HttpDelete(requestURL.toURI()));
		} catch (Throwable e) {
			requestFailed(requestURL, e);
		}

		return null;
	}

	protected T put(String path, Parameterized request) {

		final URL requestURL = requestURL(path);

		if (requestURL == null) {
			return null;
		}
		try {

			final HttpPut put = new HttpPut(requestURL.toURI());

			put.setEntity(new ParametersEntity(request));

			return sendRequest(put);
		} catch (Throwable e) {
			requestFailed(requestURL, e);
		}

		return null;
	}

	protected T post(String path, Parameterized request) {

		final URL requestURL = requestURL(path);

		if (requestURL == null) {
			return null;
		}
		try {

			final HttpPost post = new HttpPost(requestURL.toURI());

			post.setEntity(new ParametersEntity(request));

			return sendRequest(post);
		} catch (Throwable e) {
			requestFailed(requestURL, e);
		}

		return null;
	}

	protected T sendRequest(
			HttpUriRequest request)
	throws IOException, ClientProtocolException {
		return httpClient().execute(request, this);
	}

	protected Parameters parseResponse(HttpEntity entity) throws IOException {

		final String charset = getContentCharSet(entity);

		try (InputStreamReader in = new InputStreamReader(
				entity.getContent(),
				charset != null ? charset : UTF_8)) {
			return new Parameters().urlDecode(in);
		}
	}

	private URL requestURL(String path) {

		final URL collectorURL = getSettings().getCollectorURL();

		try {

			final UUID clientId = getConnection().getClientUUID();
			final URL clientURL;

			if (clientId == null) {
				clientURL = collectorURL;
			} else {
				clientURL = new URL(collectorURL, clientId.toString());
			}

			if (path == null) {
				return clientURL;
			}

			return new URL(clientURL, path);
		} catch (MalformedURLException e) {
			getConnection().requestFailed("Internal error", e);
		}

		return null;
	}

	private void requestFailed(URL requestURL, Throwable e) {
		if (e instanceof HttpResponseException) {

			final HttpResponseException http = (HttpResponseException) e;

			getConnection().requestFailed(
					"Wrong response from " + requestURL
					+ " [" + http.getStatusCode() + "]: "
					+ http.getMessage(),
					null);
		}

		getConnection().requestFailed(
				"Request to " + requestURL + " failed",
				e);
	}


}
