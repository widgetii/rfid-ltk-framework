package ru.aplix.ltk.collector.http.server.sender;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;

import ru.aplix.ltk.collector.http.server.ClrClient;


public class PingSender extends NoResponseMessageSender implements Runnable {

	private static final long PING_PERIOD = 5000L;

	private final ScheduledExecutorService executor;
	private ScheduledFuture<?> ping;


	public PingSender(ClrClient<?> client, ScheduledExecutorService executor) {
		super(client);
		this.executor = executor;
	}

	public void schedule() {
		this.ping = this.executor.scheduleAtFixedRate(
				this,
				PING_PERIOD,
				PING_PERIOD,
				TimeUnit.SECONDS);
	}

	public void reschedule() {
		if (this.ping != null) {
			this.ping.cancel(false);
		}
		schedule();
	}

	@Override
	public void run() {
		call();
	}

	@Override
	public Boolean call() {
		return get("ping");
	}

	@Override
	protected Boolean sendRequest(
			HttpUriRequest request)
	throws IOException, ClientProtocolException {
		// No need to reschedule itself.
		return httpClient().execute(request, this);
	}

}
