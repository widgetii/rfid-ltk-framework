package ru.aplix.ltk.collector.http.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.core.util.Parameterized;


public abstract class MessageSender<T>
		implements Runnable, ResponseHandler<T> {

	private final ClrClient<?> client;

	public MessageSender(ClrClient<?> client) {
		this.client = client;
	}

	public final ClrClient<?> getClient() {
		return this.client;
	}

	public final HttpClient httpClient() {
		return getClient().allProfiles().httpClient();
	}

	@Override
	public T handleResponse(
			HttpResponse response)
	throws ClientProtocolException, IOException {

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

	protected T post(Parameterized request) {

		final URL requestURL = requestURL(null);

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

		final T result = httpClient().execute(request, this);

		getClient().requestSent();

		return result;
	}

	private URL requestURL(String query) {
		try {

			final URL clientURL = getClient().getClientURL();

			if (query == null) {
				return clientURL;
			}

			return new URL(clientURL, clientURL.getPath() + query);
		} catch (MalformedURLException e) {
			getClient().requestFailed("Internal error", e);
		}
		return null;
	}

	private void requestFailed(URL requestURL, Throwable e) {
		if (e instanceof HttpResponseException) {

			final HttpResponseException http = (HttpResponseException) e;

			getClient().requestFailed(
					"Wrong response from " + requestURL
					+ " [" + http.getStatusCode() + "]: "
					+ http.getMessage(),
					null);
		}

		getClient().requestFailed(
				"Request to " + requestURL + " failed",
				e);
	}


}
