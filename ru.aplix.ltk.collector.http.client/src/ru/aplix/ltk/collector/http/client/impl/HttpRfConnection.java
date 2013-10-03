package ru.aplix.ltk.collector.http.client.impl;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import ru.aplix.ltk.collector.http.RemoteClrException;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.collector.RfTracking;
import ru.aplix.ltk.core.source.*;
import ru.aplix.ltk.osgi.Logger;


final class HttpRfConnection
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

	HttpRfConnection(HttpRfClient client, HttpRfSettings settings) {
		super(settings);
		this.client = client;
		this.settings = settings;
		generateUUID();
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

	public final Logger getLogger() {
		return getClient().getProvider().getLogger();
	}

	@Override
	public void requestRfStatus(RfStatusUpdater updater) {
		getLogger().debug(this + " Starting");
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
		getLogger().debug(this + " Stopping");
		this.statusUpdater = null;
	}

	public void updateStatus(RfStatusMessage status) {
		requestReceived();
		logStatus(status);

		final RfStatusUpdater statusUpdater = this.statusUpdater;

		if (statusUpdater != null) {
			statusUpdater.updateStatus(status);
		}
	}

	private void logStatus(RfStatusMessage status) {
		if (!status.getRfStatus().isError()) {
			getLogger().debug(this + " Status update: " + status.getRfStatus());
			return;
		}

		final String errorMessage = status.getErrorMessage();
		final Throwable cause = status.getCause();

		if (cause instanceof RemoteClrException) {

			final RemoteClrException remote = (RemoteClrException) cause;

			getLogger().error(
					this + "Remote error: " + errorMessage
					+ ". Remote exception: "
					+ remote.getRemoteExceptionClassName()
					+ ": " + remote.getMessage());
			return;
		}

		getLogger().error(this + " Remote error: " + errorMessage, cause);
	}

	public void updateTagAppearance(RfTagAppearanceRequest tagAppearance) {
		requestReceived();

		getLogger().debug(
				this + " Tag appearance changed (" + tagAppearance.getRfTag()
				+ "): " + tagAppearance.getAppearance());

		if (isConnected()) {
			this.tracking.updateTagAppearance(tagAppearance);
			this.lastTagEventId = tagAppearance.getEventId();
		}
	}

	public void ping() {
		getLogger().debug(this + " Ping");
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

		reconnect();
		updateStatus(new RfError(
				clientUUID != null ? clientUUID.toString() : null,
				message,
				cause));
	}

	void connectionEstablished() {
		this.connected = true;
		updateStatus(new RfConnected(getClientUUID().toString()));
	}

	void connectionLost() {
		getLogger().error(this + " Connection lost");
		reconnect();
	}

	void shutdown() {
		this.reconnector.cancel();
		new DisconnectRequest(this).send();
		disconnect();
		this.executor.shutdown();
	}

	private void generateUUID() {
		this.clientUUID = randomUUID();
		getClient().add(this);
	}

	private void reconnect() {
		disconnect();
		generateUUID();
	}

	private void disconnect() {
		this.connected = false;
		getClient().remove(this);
	}

}
