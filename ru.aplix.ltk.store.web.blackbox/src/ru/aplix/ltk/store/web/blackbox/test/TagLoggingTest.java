package ru.aplix.ltk.store.web.blackbox.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;

import java.io.IOException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.store.web.blackbox.rule.*;


public class TagLoggingTest {

	@Rule
	public TestReceiver receiver =
			new TestReceiver("blackbox@blackbox-log", true);

	@Rule
	public HttpConnection connection = new HttpConnection();

	@Test
	public void loggedTagReported() throws Exception {

		final CollectorConsumer statusConsumer = this.receiver.subscribe();
		final RfTagAppearanceRequest request = sendTag();
		final TagConsumer consumer = statusConsumer.requestTagAppearance();

		consumer.lastMessage();

		final List<? extends RfTagAppearanceMessage> events =
				this.receiver.getRfReceiver().allEvents();

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
				this.receiver.getRfReceiver().allEvents();

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
		return this.connection.sendTag(0, RF_TAG_APPEARED);
	}

}
