package ru.aplix.ltk.store.web.blackbox.rule;

import static java.lang.System.currentTimeMillis;
import static org.apache.http.util.EntityUtils.consume;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.store.web.blackbox.rule.TestTags.randomTag;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.junit.rules.ExternalResource;

import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.core.collector.RfTagAppearance;


public class HttpConnection extends ExternalResource {

	private HttpClient httpClient;

	public final HttpClient getHttpClient() {
		return this.httpClient;
	}

	public HttpPost post(String path) {
		return new HttpPost("http://localhost:28080/blackbox/" + path);
	}

	public RfTagAppearanceRequest tagRequest(
			long eventId,
			RfTagAppearance appearance) {

		final RfTagAppearanceRequest request = new RfTagAppearanceRequest();

		request.setTimestamp(currentTimeMillis());
		request.setAppearance(appearance);
		request.setEventId(eventId);
		request.setRfTag(randomTag());

		return request;
	}

	public RfTagAppearanceRequest sendTag(
			long eventId,
			RfTagAppearance appearance)
	throws IOException {
		return sendTag(tagRequest(eventId, appearance));
	}

	public RfTagAppearanceRequest sendTag(
			RfTagAppearanceRequest request)
	throws IOException {

		final HttpPost post = post("tag");

		post.setEntity(new ParametersEntity(request));

		final HttpResponse response = getHttpClient().execute(post);

		assertThat(
				response.getStatusLine().getReasonPhrase(),
				response.getStatusLine().getStatusCode(),
				is(200));

		consume(response.getEntity());

		return request;
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
