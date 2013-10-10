package ru.aplix.ltk.store.web.blackbox.test;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;
import static ru.aplix.ltk.store.web.blackbox.rule.TestTags.randomTag;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.store.web.blackbox.rule.HttpConnection;
import ru.aplix.ltk.store.web.blackbox.rule.TagConsumer;
import ru.aplix.ltk.store.web.blackbox.rule.TestReceiver;


public class TagSendingTest {

	@Rule
	public TestReceiver receiver = new TestReceiver("blackbox", true);

	@Rule
	public HttpConnection connection = new HttpConnection();

	@Test
	public void tagAppeared() throws Exception {

		final TagConsumer consumer = subscribe();
		final HttpPost post = this.connection.post("tag");
		final RfTagAppearanceRequest request = new RfTagAppearanceRequest();

		request.setTimestamp(currentTimeMillis());
		request.setAppearance(RF_TAG_APPEARED);
		request.setEventId(1);
		request.setRfTag(randomTag());

		post.setEntity(new ParametersEntity(request));

		final HttpResponse response =
				this.connection.getHttpClient().execute(post);

		assertThat(
				response.getStatusLine().getReasonPhrase(),
				response.getStatusLine().getStatusCode(),
				is(200));

		final RfTagAppearanceMessage message = consumer.nextMessage();

		assertThat(message.getTimestamp(), is(request.getTimestamp()));
		assertThat(message.getEventId(), is(request.getEventId()));
		assertThat(message.getAppearance(), is(request.getAppearance()));
		assertThat(message.getRfTag(), is(request.getRfTag()));
	}

	@Test
	public void tagDisappeared() throws Exception {

		final TagConsumer consumer = subscribe();
		final HttpPost post = this.connection.post("tag");
		final RfTagAppearanceRequest request = new RfTagAppearanceRequest();

		request.setTimestamp(currentTimeMillis());
		request.setAppearance(RF_TAG_DISAPPEARED);
		request.setEventId(2);
		request.setRfTag(randomTag());

		post.setEntity(new ParametersEntity(request));

		final HttpResponse response =
				this.connection.getHttpClient().execute(post);

		assertThat(
				response.getStatusLine().getReasonPhrase(),
				response.getStatusLine().getStatusCode(),
				is(200));

		final RfTagAppearanceMessage message = consumer.nextMessage();

		assertThat(message.getTimestamp(), is(request.getTimestamp()));
		assertThat(message.getEventId(), is(request.getEventId()));
		assertThat(message.getAppearance(), is(request.getAppearance()));
		assertThat(message.getRfTag(), is(request.getRfTag()));
	}

	private TagConsumer subscribe() {
		return this.receiver.subscribe().requestTagAppearance();
	}

}
