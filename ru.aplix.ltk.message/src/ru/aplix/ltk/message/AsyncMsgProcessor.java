package ru.aplix.ltk.message;

import java.util.concurrent.Executor;


final class AsyncMsgProcessor<H extends MsgHandle<H, M>, M>
		extends MsgProxy<H, M> {

	private final Executor executor;

	AsyncMsgProcessor(MsgConsumer<H, M> consumer, Executor executor) {
		super(consumer);
		this.executor = executor;
	}

	@Override
	public void messageReceived(final M message) {
		this.executor.execute(new Runnable() {
			@Override
			public void run() {
				getProxied().messageReceived(message);
			}
		});
	}

}
