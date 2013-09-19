package ru.aplix.ltk.collector.http.client.impl;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


class HttpReconnector implements Runnable {

	private final HttpRfConnection connection;
	private ScheduledFuture<?> future;

	HttpReconnector(HttpRfConnection connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		this.connection.connectionLost();
		new ConnectRequest(this.connection).send();
	}

	public void schedule() {

		final long reconnectionDelay =
				this.connection.getSettings().getReconnectionDelay();

		this.future = this.connection.getExecutor().scheduleAtFixedRate(
				this,
				reconnectionDelay,
				reconnectionDelay,
				TimeUnit.MILLISECONDS);
	}

	public void reschedule() {
		cancel();
		schedule();
	}

	public void cancel() {
		if (this.future != null) {
			this.future.cancel(false);
		}
	}

}
