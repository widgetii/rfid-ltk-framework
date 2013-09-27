package ru.aplix.ltk.collector.http.server;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;

import ru.aplix.ltk.collector.http.ClrClientId;
import ru.aplix.ltk.collector.http.ClrClientRequest;
import ru.aplix.ltk.collector.http.server.sender.PingSender;
import ru.aplix.ltk.collector.http.server.sender.StatusSender;
import ru.aplix.ltk.collector.http.server.sender.TagAppearanceSender;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.osgi.Logger;


public class ClrClient<S extends RfSettings>
		implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

	private final ClrProfile<S> profile;
	private final ClrClientId clientId;
	private PingSender ping;
	private ClrClientRequest clientRequest;
	private RfCollectorHandle collectorHandle;
	private ScheduledExecutorService executor;

	public ClrClient(ClrProfile<S> profile, ClrClientId id) {
		this.profile = profile;
		this.clientId = id;
	}

	public final AllClrProfiles allProfiles() {
		return getProfile().allProfiles();
	}

	public final ClrProfile<S> getProfile() {
		return this.profile;
	}

	public final ClrClientId getId() {
		return this.clientId;
	}

	public final URL getClientURL() {
		return this.clientRequest.getClientURL();
	}

	public final Logger log() {
		return allProfiles().getCollectorService().log();
	}

	public synchronized void start(ClrClientRequest clientRequest) {
		this.clientRequest = clientRequest;
		getProfile().connect().getCollector().subscribe(this);
	}

	public synchronized void stop() {
		if (this.collectorHandle != null) {
			try {
				this.collectorHandle.unsubscribe();
			} finally {
				this.collectorHandle = null;
			}
		}
	}

	@Override
	public void consumerSubscribed(RfCollectorHandle handle) {
		this.collectorHandle = handle;
		this.executor = newSingleThreadScheduledExecutor();
		handle.requestTagAppearance(
				new TagListener(),
				this.clientRequest.getLastTagEventId());
		this.ping = new PingSender(this, this.executor);
		this.ping.schedule();
	}

	@Override
	public void messageReceived(RfStatusMessage message) {
		this.executor.submit(new StatusSender(this, message));
	}

	@Override
	public void consumerUnsubscribed(RfCollectorHandle handle) {
		if (this.executor != null) {
			try {
				this.executor.shutdownNow();
			} finally {
				this.executor = null;
			}
		}
	}

	final void requestSent() {
		this.ping.reschedule();
	}

	final void requestFailed(String message, Throwable e) {
		log().error(
				getId() + ": " + message + ". Disconnecting",
				e);
		getProfile().disconnectClient(getId().getUUID());
	}

	private void sendTag(RfTagAppearanceMessage message) {
		this.executor.submit(new TagAppearanceSender(this, message));
	}

	private final class TagListener implements MsgConsumer<
			RfTagAppearanceHandle,
			RfTagAppearanceMessage> {

		@Override
		public void consumerSubscribed(RfTagAppearanceHandle handle) {
		}

		@Override
		public void messageReceived(RfTagAppearanceMessage message) {
			sendTag(message);
		}

		@Override
		public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
		}

	}

}
