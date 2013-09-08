package ru.aplix.ltk.collector.http.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.core.util.Parameterized;


public abstract class MessageSender<T>
		implements Callable<T>, ResponseHandler<T> {

	private final ClrClient<?> client;

	public MessageSender(ClrClient<?> client) {
		this.client = client;
	}

	public final ClrClient<?> getClient() {
		return this.client;
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

	protected T post(String path, Parameterized request) {

		final URL requestURL = requestURL(path);

		if (requestURL == null) {
			return null;
		}
		try {

			final HttpPost post = new HttpPost(requestURL.toURI());

			post.setEntity(new ParametersEntity(request));

			return httpClient().execute(post, this);
		} catch (HttpResponseException e) {
			getClient().requestFailed(
					"Wrong response from " + requestURL
					+ " [" + e.getStatusCode() + "]: "
					+ e.getMessage(),
					null);
		} catch (Throwable e) {
			getClient().requestFailed(
					"Request to " + requestURL + " failed",
					e);
		}

		return null;
	}

	private URL requestURL(String path) {
		try {
			return new URL(
					new URL(
							getClient().getClientURL(),
							getClient().getId().getUUID().toString()),
					path);
		} catch (MalformedURLException e) {
			getClient().requestFailed("Internal error", e);
		}
		return null;
	}

	private final HttpClient httpClient() {
		return getClient().allProfiles().httpClient();
	}

}
