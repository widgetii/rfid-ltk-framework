package ru.aplix.ltk.store.web.blackbox.test;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.store.web.blackbox.rule.TestTags.randomTag;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.store.web.blackbox.rule.*;


public class TagLoggingTest {

	@Rule
	public TestReceiver receiver = new TestReceiver("blackbox-log", true);

	@Rule
	public HttpConnection connection = new HttpConnection();

	@Test
	public void loggedTagReported() throws Exception {

		final CollectorConsumer statusConsumer = this.receiver.subscribe();
		final RfTagAppearanceRequest request = sendTag();
		final TagConsumer consumer = statusConsumer.requestTagAppearance();

		consumer.lastMessage();

		final List<? extends RfTagAppearanceMessage> events =
				this.receiver.getRfReceiver().loadEvents(0, Integer.MAX_VALUE);

		assertThat("No tags stored", events.isEmpty(), is(false));

		final RfTagAppearanceMessage first = events.get(0);

		assertThat(first.getEventId(), is(1L));

		final RfTagAppearanceMessage last = events.get(events.size() - 1);

		assertThat(last.getRfTag(), is(request.getRfTag()));
		assertThat(last.getTimestamp(), is(request.getTimestamp()));
		assertThat(last.getAppearance(), is(request.getAppearance()));
	}

	@Test
	public void reportAfterLogged() throws Exception {

		final CollectorConsumer statusConsumer = this.receiver.subscribe();
		final TagConsumer consumer = statusConsumer.requestTagAppearance();

		consumer.lastMessage();

		final List<? extends RfTagAppearanceMessage> events =
				this.receiver.getRfReceiver().loadEvents(0, Integer.MAX_VALUE);

		assertThat("No tags stored", events.isEmpty(), is(false));

		final RfTagAppearanceMessage last =
				events.isEmpty() ? null : events.get(events.size() - 1);

		final RfTagAppearanceRequest request = sendTag();
		final RfTagAppearanceMessage message = consumer.nextMessage();

		assertThat(message.getRfTag(), is(request.getRfTag()));
		assertThat(message.getTimestamp(), is(request.getTimestamp()));
		assertThat(message.getAppearance(), is(request.getAppearance()));

		if (last == null) {
			assertThat(message.getEventId(), is(1L));
		} else {
			assertThat(message.getEventId(), is(last.getEventId() + 1));
		}
	}

	private RfTagAppearanceRequest sendTag() throws IOException {

		final HttpPost post = this.connection.post("tag");
		final RfTagAppearanceRequest request = new RfTagAppearanceRequest();

		request.setTimestamp(currentTimeMillis());
		request.setAppearance(RF_TAG_APPEARED);
		request.setEventId(0);
		request.setRfTag(randomTag());

		post.setEntity(new ParametersEntity(request));

		final HttpResponse response =
				this.connection.getHttpClient().execute(post);

		assertThat(
				response.getStatusLine().getReasonPhrase(),
				response.getStatusLine().getStatusCode(),
				is(200));

		return request;
	}

}
