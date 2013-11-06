package ru.aplix.ltk.store.web.blackbox.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.store.web.blackbox.rule.HttpConnection;
import ru.aplix.ltk.store.web.blackbox.rule.TagConsumer;
import ru.aplix.ltk.store.web.blackbox.rule.TestReceiver;


public class TagSendingTest {

	@Rule
	public TestReceiver receiver = new TestReceiver("blackbox@blackbox", true);

	@Rule
	public HttpConnection connection = new HttpConnection();

	@Test
	public void tagAppeared() throws Exception {

		final TagConsumer consumer = subscribe();
		final RfTagAppearanceRequest request =
				this.connection.sendTag(1, RF_TAG_APPEARED);
		final RfTagAppearanceMessage message = consumer.nextMessage();

		assertThat(message.getTimestamp(), is(request.getTimestamp()));
		assertThat(message.getEventId(), is(request.getEventId()));
		assertThat(message.getAppearance(), is(request.getAppearance()));
		assertThat(message.getRfTag(), is(request.getRfTag()));
	}

	@Test
	public void tagDisappeared() throws Exception {

		final TagConsumer consumer = subscribe();
		final RfTagAppearanceRequest request =
				this.connection.sendTag(2, RF_TAG_DISAPPEARED);
		final RfTagAppearanceMessage message = consumer.nextMessage();

		assertThat(message.getTimestamp(), is(request.getTimestamp()));
		assertThat(message.getEventId(), is(request.getEventId()));
		assertThat(message.getAppearance(), is(request.getAppearance()));
		assertThat(message.getRfTag(), is(request.getRfTag()));
	}

	@Test
	public void hideVisibleTags() throws Exception {

		final TagConsumer consumer = subscribe();
		final RfTagAppearanceRequest appearance =
				this.connection.sendTag(1, RF_TAG_APPEARED);
		final RfTagAppearanceRequest initReq =
				this.connection.tagRequest(2, RF_TAG_APPEARED);

		initReq.setInitialEvent(true);
		this.connection.sendTag(initReq);

		final RfTagAppearanceMessage message1 = consumer.nextMessage();

		assertThat(message1.getEventId(), is(appearance.getEventId()));

		final RfTagAppearanceMessage message2 = consumer.nextMessage();

		assertThat(message2.getEventId(), is(initReq.getEventId()));
		assertThat(message2.isInitialEvent(), is(true));

		final List<? extends RfTagAppearanceMessage> events =
				this.receiver.getRfReceiver().allEvents();

		assertThat(events.size(), is(3));

		final RfTagAppearanceMessage disappearance = events.get(1);

		assertThat(disappearance.getTimestamp(), is(initReq.getTimestamp()));
		assertThat(disappearance.getEventId(), is(0L));
		assertThat(disappearance.getAppearance(), is(RF_TAG_DISAPPEARED));
		assertThat(disappearance.getRfTag(), is(appearance.getRfTag()));
		assertThat(disappearance.isInitialEvent(), is(false));
	}

	private TagConsumer subscribe() {
		return this.receiver.subscribe().requestTagAppearance();
	}

}
