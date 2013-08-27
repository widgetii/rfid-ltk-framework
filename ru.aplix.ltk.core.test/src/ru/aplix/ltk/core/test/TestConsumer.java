package ru.aplix.ltk.core.test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgHandle;


public class TestConsumer<H extends MsgHandle<H, M>, M>
		implements MsgConsumer<H, M> {

	private final LinkedBlockingQueue<M> messages =
			new LinkedBlockingQueue<>();
	private H handle;

	public final LinkedBlockingQueue<M> getMessages() {
		return this.messages;
	}

	@Override
	public void consumerSubscribed(H handle) {
		this.handle = handle;
	}

	@Override
	public void messageReceived(M message) {
		this.messages.add(message);
	}

	@Override
	public void consumerUnsubscribed(H handle) {
		this.handle = null;
	}

	public final H handle() {
		return this.handle;
	}

	public M nextMessage() {
		try {

			final M message = this.messages.poll(1, TimeUnit.SECONDS);

			assertThat(message, notNullValue());

			return message;
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public void noMoreMessages() {
		assertThat(this.messages.peek(), nullValue());
	}

}
