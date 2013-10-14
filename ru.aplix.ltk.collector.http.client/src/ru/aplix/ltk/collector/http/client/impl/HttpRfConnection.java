package ru.aplix.ltk.collector.http.client.impl;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.collector.http.client.impl.monitor.HttpRfClientMtr;
import ru.aplix.ltk.collector.http.client.impl.monitor.HttpRfClientMtrTarget;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.collector.RfTracking;
import ru.aplix.ltk.core.source.*;
import ru.aplix.ltk.monitor.MonitoringTracker;


public final class HttpRfConnection
		extends RfConnection
		implements RfSource, RfTracker {

	private ScheduledExecutorService executor;
	private final HttpRfClient client;
	private final HttpRfSettings settings;
	private final HttpReconnector reconnector = new HttpReconnector(this);
	private UUID clientUUID;
	private long lastTagEventId;
	private volatile RfStatusUpdater statusUpdater;
	private volatile RfTracking tracking;
	private volatile boolean connected;
	private MonitoringTracker<HttpRfClientMtr> tracker;

	HttpRfConnection(HttpRfClient client, HttpRfSettings settings) {
		super(settings);
		this.client = client;
		this.settings = settings;
		generateUUID();
		this.tracker = new MonitoringTracker<>(
				client.getProvider().getContext(),
				new HttpRfClientMtrTarget(this));
		this.tracker.open();
	}

	public final long getLastTagEventId() {
		return this.lastTagEventId;
	}

	public final void setLastTagEventId(long lastTagEventId) {
		this.lastTagEventId = lastTagEventId;
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
		return this.connected;
	}

	public final HttpRfClientMtr getMonitoring() {
		return this.tracker.getService();
	}

	@Override
	public void requestRfStatus(RfStatusUpdater updater) {
		getMonitoring().statusRequested();
		this.statusUpdater = updater;
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
		this.executor = newSingleThreadScheduledExecutor();
		new ConnectRequest(this).send();
		this.reconnector.schedule();
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {
	}

	@Override
	public void stopRfTracking() {
		shutdown();
	}

	@Override
	public void rejectRfData() {
	}

	@Override
	public void rejectRfStatus() {
		getMonitoring().statusRejected();
		this.statusUpdater = null;
	}

	public void updateStatus(RfStatusMessage status) {
		requestReceived();
		getMonitoring().status(status);

		final RfStatusUpdater statusUpdater = this.statusUpdater;

		if (statusUpdater != null) {
			statusUpdater.updateStatus(status);
		}
	}

	public void updateTagAppearance(RfTagAppearanceRequest tagAppearance) {
		requestReceived();

		getMonitoring().tag(tagAppearance);

		if (isConnected()) {
			this.tracking.updateTagAppearance(tagAppearance);
			this.lastTagEventId = tagAppearance.getEventId();
		}
	}

	public void ping() {
		getMonitoring().ping();
		requestReceived();
	}

	private void requestReceived() {
		this.reconnector.reschedule();
	}

	@Override
	public String toString() {
		if (this.client == null) {
			return super.toString();
		}
		if (this.clientUUID == null) {
			return "HttpRfConnection[" + getSettings().getCollectorURL() + ']';
		}
		return "HttpRfConnection["
				+ this.clientUUID + "->"
				+ getSettings().getCollectorURL() + ']';
	}

	@Override
	protected RfCollector createCollector() {
		return new HttpRfCollector(this);
	}

	@Override
	protected RfSource createSource() {
		return this;
	}

	void requestFailed(String message, Throwable cause) {
		getClient().remove(this);

		final UUID clientUUID = getClientUUID();

		updateStatus(new RfError(
				clientUUID != null ? clientUUID.toString() : null,
				message,
				cause));
		reconnect();
	}

	void connectionEstablished() {
		this.connected = true;
		getMonitoring().connected();
		updateStatus(new RfConnected(getClientUUID().toString()));
	}

	void connectionLost() {
		getMonitoring().connectionLost();
		reconnect();
	}

	void shutdown() {
		getMonitoring().shutdown();
		this.reconnector.cancel();
		new DisconnectRequest(this).send();
		disconnect();
		getExecutor().submit(new Runnable() {
			@Override
			public void run() {
				getMonitoring().stop();
				HttpRfConnection.this.tracker.close();
			}
		});
		this.executor.shutdown();
	}

	private void generateUUID() {
		this.clientUUID = randomUUID();
		getClient().add(this);
	}

	private void reconnect() {
		disconnect();
		getMonitoring().reconnect();
		generateUUID();
	}

	private void disconnect() {
		getMonitoring().disconnected();
		this.connected = false;
		getClient().remove(this);
	}

}
