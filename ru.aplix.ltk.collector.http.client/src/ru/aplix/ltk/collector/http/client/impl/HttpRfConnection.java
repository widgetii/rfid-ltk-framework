package ru.aplix.ltk.collector.http.client.impl;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.source.*;


public class HttpRfConnection extends RfConnection implements RfSource {

	private ScheduledExecutorService executor;
	private final HttpRfClient client;
	private final HttpRfSettings settings;
	private final HttpReconnector reconnector = new HttpReconnector(this);
	private UUID clientUUID;
	private String clientPath;
	private volatile RfStatusUpdater statusUpdater;
	private volatile RfDataReceiver dataReceiver;

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

	public String getClientPath() {
		return this.clientPath;
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
		this.dataReceiver = receiver;
	}

	@Override
	public void rejectRfData() {
		this.dataReceiver = null;
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

		final RfDataReceiver dataReceiver = this.dataReceiver;

		if (dataReceiver != null) {

			final HttpRfCollector collector = (HttpRfCollector) getCollector();

			collector.updateTagAppearance(tagAppearance);
		}
	}

	public void ping() {
		this.reconnector.reschedule();
	}

	@Override
	protected HttpRfCollector createCollector() {
		return new HttpRfCollector(this);
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

	void connectionEstablished(UUID clientUUID) throws MalformedURLException {
		this.clientUUID = clientUUID;
		this.clientPath = new URL(
				getSettings().getClientURL(),
				clientUUID.toString())
				.toString();
		getClient().connected(this);
		updateStatus(new RfConnected(clientUUID.toString()));
	}

}
