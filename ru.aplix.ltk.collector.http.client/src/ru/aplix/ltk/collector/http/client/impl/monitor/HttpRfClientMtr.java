package ru.aplix.ltk.collector.http.client.impl.monitor;

import ru.aplix.ltk.collector.http.RemoteClrException;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.impl.HttpRfConnection;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.monitor.Monitoring;
import ru.aplix.ltk.monitor.MonitoringEvent;


public class HttpRfClientMtr extends Monitoring {

	private final MonitoringEvent connectionTimeout = error();
	private final MonitoringEvent reconnect =
			note()
			.afterTimeout(
					10000L,
					this.connectionTimeout,
					"Reconnection timeout");
	private final MonitoringEvent connecting =
			info()
			.forgetOnUpdate(this.reconnect)
			.afterTimeout(
					45000L,
					this.connectionTimeout,
					"Connection timeout");
	private final MonitoringEvent connectionLost = error();
	private final MonitoringEvent connected =
			info()
			.forgetWhenOccurred(this.connecting)
			.forgetWhenOccurred(this.connectionLost);
	private final MonitoringEvent disconnected = warning();
	private final MonitoringEvent alreadyConnected = warning();
	private final MonitoringEvent shutdown =
			info()
			.afterTimeout(10000, error(), "Shutdown timed out");

	HttpRfClientMtr() {
	}

	public final HttpRfConnection getConnection() {

		final HttpRfClientMtrTarget target =
				(HttpRfClientMtrTarget) getTarget();

		return target.getConnection();
	}

	public void statusRequested() {
		getLogger().debug(this + " Status requested");
	}

	public void statusRejected() {
		getLogger().debug(this + " Status rejected");
	}

	public void ping() {
		getLogger().debug(this + " Ping received");
	}

	public void tag(RfTagAppearanceRequest tagAppearance) {
		getLogger().debug(
				this + " Tag appearance changed (" + tagAppearance.getRfTag()
				+ "): " + tagAppearance.getAppearance());
	}

	public void status(RfStatusMessage status) {
		if (!status.getRfStatus().isError()) {
			getLogger().debug(this + " Status update: " + status.getRfStatus());
			return;
		}

		final String errorMessage = status.getErrorMessage();
		final Throwable cause = status.getCause();

		if (cause instanceof RemoteClrException) {

			final RemoteClrException remote = (RemoteClrException) cause;

			getLogger().error(
					this + " Remote error: " + errorMessage
					+ ". Remote exception: "
					+ remote.getRemoteExceptionClassName()
					+ ": " + remote.getMessage());
			return;
		}

		getLogger().error(this + " Remote error: " + errorMessage, cause);
	}

	public void connecting() {
		this.connecting.occur("Connecting");
	}

	public void connected() {
		this.connected.occur("Connected");
	}

	public void alreadyConnected() {
		this.alreadyConnected.occur("Already connected");
	}

	public void connectionLost() {
		this.connectionLost.occur("Connection lost");
	}

	public void reconnect() {
		this.reconnect.occur("Reconnecting");
	}

	public void shutdown() {
		this.shutdown.occur("Shutting down");
	}

	public void closingConnection() {
		this.shutdown.occur("Closing connection");
	}

	public void disconnected() {
		this.disconnected.occur("Disconnected");
	}

	@Override
	public String toString() {
		if (getTarget() == null) {
			return super.toString();
		}
		return getConnection().toString();
	}

	@Override
	protected void init() {
	}

}
