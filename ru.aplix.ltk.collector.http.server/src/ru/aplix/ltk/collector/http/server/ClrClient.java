package ru.aplix.ltk.collector.http.server;

import java.net.URL;

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
import ru.aplix.ltk.core.util.SingleThreadExecutor;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.osgi.Logger;


public class ClrClient<S extends RfSettings>
		implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

	private final ClrProfile<S> profile;
	private final ClrClientId clientId;
	private PingSender ping;
	private ClrClientRequest clientRequest;
	private RfCollectorHandle collectorHandle;
	private SingleThreadExecutor executor;

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

	public final SingleThreadExecutor getExecutor() {
		return this.executor;
	}

	public final Logger log() {
		return allProfiles().log();
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
		this.executor = new SingleThreadExecutor(128);
		this.executor.getThread().setName("ClrClient-" + getId());
		this.ping = new PingSender(this);
		this.ping.schedule();
		handle.requestTagAppearance(
				new TagListener(),
				this.clientRequest.getLastTagEventId());
	}

	@Override
	public void messageReceived(RfStatusMessage message) {
		System.err.println("(!) status " + this.executor);
		this.executor.submit(new StatusSender(this, message));
		System.err.println("(!) status added " + this.executor);
	}

	@Override
	public void consumerUnsubscribed(RfCollectorHandle handle) {
		this.ping.cancel();
		this.executor.close();
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
		System.err.println("(!) tag");
		this.executor.submit(new TagAppearanceSender(this, message));
		System.err.println("(!) tag added");
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
