package ru.aplix.ltk.collector.http.client.impl;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.collector.RfTracking;
import ru.aplix.ltk.core.source.*;


public class HttpRfConnection
		extends RfConnection
		implements RfSource, RfTracker {

	private ScheduledExecutorService executor;
	private final HttpRfClient client;
	private final HttpRfSettings settings;
	private final HttpReconnector reconnector = new HttpReconnector(this);
	private UUID clientUUID;
	private volatile RfStatusUpdater statusUpdater;
	private volatile RfTracking tracking;

	public HttpRfConnection(HttpRfClient client, HttpRfSettings settings) {
		super(settings);
		this.client = client;
		this.settings = settings;
	}

	public final ScheduledExecutorService getExecutor() {
		return this.executor;
	}

	public final HttpRfClient getClient() {
		return this.client;
	}

	public final HttpRfSettings getSettings() {
		return this.settings;
	}

	public final UUID getClientUUID() {
		return this.clientUUID;
	}

	public final boolean isConnected() {
		return getClientUUID() != null;
	}

	@Override
	public void requestRfStatus(RfStatusUpdater updater) {
		this.statusUpdater = updater;
		this.executor = newSingleThreadScheduledExecutor();
		new ConnectRequest(this).send();
		this.reconnector.schedule();
	}

	@Override
	public void requestRfData(RfDataReceiver receiver) {
	}

	@Override
	public void initRfTracker(RfTracking tracking) {
		this.tracking = tracking;
	}

	@Override
	public void startRfTracking() {
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {
	}

	@Override
	public void stopRfTracking() {
		this.tracking = null;
	}

	@Override
	public void rejectRfData() {
	}

	@Override
	public void rejectRfStatus() {
		this.statusUpdater = null;
		new DisconnectRequest(this).send();
		this.executor.shutdown();
	}

	public void updateStatus(RfStatusMessage status) {
		ping();

		final RfStatusUpdater statusUpdater = this.statusUpdater;

		if (statusUpdater != null) {
			statusUpdater.updateStatus(status);
		}
	}

	public void updateTagAppearance(RfTagAppearanceRequest tagAppearance) {
		ping();

		final RfTracking tracking = this.tracking;

		if (tracking != null) {
			tracking.updateTagAppearance(tagAppearance);
		}
	}

	public void ping() {
		this.reconnector.reschedule();
	}

	@Override
	protected RfCollector createCollector() {
		return new RfCollector(this, this);
	}

	@Override
	protected RfSource createSource() {
		return this;
	}

	void requestFailed(String message, Throwable cause) {
		getClient().disconnected(this);

		final UUID clientUUID = getClientUUID();

		this.clientUUID = null;
		updateStatus(new RfError(
				clientUUID != null ? clientUUID.toString() : null,
				message,
				cause));
	}

	void connectionEstablished(UUID clientUUID) {
		this.clientUUID = clientUUID;
		getClient().connected(this);
		updateStatus(new RfConnected(clientUUID.toString()));
	}

}
