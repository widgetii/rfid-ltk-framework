package ru.aplix.ltk.message.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;


public class SubscriptionsTest {

	private static final String TEST_MESSAGE_1 = "Test message 1";
	private static final String TEST_MESSAGE_2 = "Test message 2";
	private TestSubscriptions subscriptions;

	@Test
	public void subscribe() {

		final TestConsumer consumer =
				new TestConsumer(TEST_MESSAGE_1, TEST_MESSAGE_2);
		final TestHandle handle = this.subscriptions.subscribe(consumer);

		assertThat(consumer.isSubscribed(), is(true));
		assertThat(consumer.isUnsubscribed(), is(false));
		assertThat(handle.isSubscribed(), is(true));
		assertThat(
				(Object) handle.getConsumer(),
				sameInstance((Object) consumer));
		assertThat(consumer.getHandle(), sameInstance(handle));
		assertThat(this.subscriptions.isEmpty(), is(false));

		this.subscriptions.sendMessage(TEST_MESSAGE_1);
		this.subscriptions.sendMessage(TEST_MESSAGE_2);

		consumer.ensureAllMessagesReceived();
	}

	@Test
	public void unsubscribe() {

		final TestConsumer consumer = new TestConsumer(TEST_MESSAGE_1);
		final TestHandle handle = this.subscriptions.subscribe(consumer);

		this.subscriptions.sendMessage(TEST_MESSAGE_1);

		handle.unsubscribe();

		assertThat(consumer.isUnsubscribed(), is(true));
		assertThat(handle.isSubscribed(), is(false));
		assertThat(this.subscriptions.isEmpty(), is(true));

		this.subscriptions.sendMessage(TEST_MESSAGE_2);

		consumer.ensureAllMessagesReceived();
	}

	@Test
	public void unsubscribeAll() {

		final TestConsumer consumer1 = new TestConsumer(TEST_MESSAGE_1);
		final TestHandle handle1 = this.subscriptions.subscribe(consumer1);

		final TestConsumer consumer2 = new TestConsumer(TEST_MESSAGE_1);
		final TestHandle handle2 = this.subscriptions.subscribe(consumer2);

		this.subscriptions.sendMessage(TEST_MESSAGE_1);

		this.subscriptions.unsubscribeAll();

		assertThat(consumer1.isUnsubscribed(), is(true));
		assertThat(handle1.isSubscribed(), is(false));
		assertThat(consumer2.isUnsubscribed(), is(true));
		assertThat(handle2.isSubscribed(), is(false));
		assertThat(this.subscriptions.isEmpty(), is(true));

		this.subscriptions.sendMessage(TEST_MESSAGE_2);

		consumer1.ensureAllMessagesReceived();
		consumer2.ensureAllMessagesReceived();
	}

	@Before
	public void setup() {
		this.subscriptions = new TestSubscriptions();
	}

}
