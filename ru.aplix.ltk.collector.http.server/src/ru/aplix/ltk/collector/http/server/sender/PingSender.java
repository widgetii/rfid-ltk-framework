package ru.aplix.ltk.collector.http.server.sender;

import static java.lang.System.currentTimeMillis;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;

import ru.aplix.ltk.collector.http.server.ClrClient;


public class PingSender extends NoResponseMessageSender {

	private static final long PING_PERIOD = 5000;

	private Thread thread;

	private long nextExecution;

	public PingSender(ClrClient<?> client) {
		super(client);
		this.thread = new Thread(this);
	}

	public void schedule() {
		this.thread = new Thread("PingSender-" + getClient().getId()) {
			@Override
			public void run() {
				sendPings();
			}
		};
		reschedule();
		this.thread.start();
	}

	public synchronized void reschedule() {

		final long nextExecution = currentTimeMillis() + PING_PERIOD;

		if (this.nextExecution < nextExecution) {
			this.nextExecution = nextExecution;
		}
	}

	public synchronized void cancel() {
		this.thread = null;
		notifyAll();
	}

	@Override
	public void run() {
		get("?client=" + getClient().getId().getUUID().toString());
	}

	@Override
	protected Boolean sendRequest(
			HttpUriRequest request)
	throws IOException, ClientProtocolException {
		// No need to reschedule itself.
		return httpClient().execute(request, this);
	}

	private void sendPings() {
		while (delay()) {
			reschedule();
			getClient().getExecutor().offer(this);
		}
	}

	private boolean delay() {
		for (;;) {
			synchronized (this) {
				if (this.thread == null) {
					return false;
				}

				final long left = this.nextExecution - currentTimeMillis();

				if (left <= 0) {
					return true;
				}
				try {
					wait(left);
				} catch (InterruptedException e) {
					return false;
				}
			}
		}
	}

}
