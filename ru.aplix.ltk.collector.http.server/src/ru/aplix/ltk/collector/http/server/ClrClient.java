package ru.aplix.ltk.collector.http.server;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;

import ru.aplix.ltk.collector.http.ClrClientId;
import ru.aplix.ltk.collector.http.ClrClientRequest;
import ru.aplix.ltk.collector.http.server.sender.StatusSender;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.osgi.Logger;


public class ClrClient<S extends RfSettings>
		implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

	private final ClrProfile<S> profile;
	private final ClrClientId clientId;
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

	protected final void requestFailed(String message, Throwable e) {
		log().error(
				getId() + ": " + message + ". Disconnecting",
				e);
		getProfile().disconnectClient(getId().getUUID());
	}

}
