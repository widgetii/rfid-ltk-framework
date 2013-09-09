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
		new ConnectRequest(this.connection).send();
	}

	public void schedule() {
		this.future = this.connection.getExecutor().scheduleAtFixedRate(
				this,
				15,
				15,
				TimeUnit.SECONDS);
	}

	public void reschedule() {
		if (this.future != null) {
			this.future.cancel(false);
		}
		schedule();
	}

}
