package ru.aplix.ltk.collector.http.client.impl.monitor;

import ru.aplix.ltk.collector.http.RemoteClrException;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.impl.HttpRfConnection;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.monitor.Monitoring;
import ru.aplix.ltk.monitor.MonitoringEvent;
import ru.aplix.ltk.monitor.MonitoringSeverity;


public class HttpRfClientMtr extends Monitoring {

	private final MonitoringEvent connectionTimeout =
			new MonitoringEvent(this, MonitoringSeverity.ERROR);
	private final MonitoringEvent reconnect =
			new MonitoringEvent(this, MonitoringSeverity.OK)
			.afterTimeout(
					10000L,
					this.connectionTimeout,
					"Reconnection timeout");
	private final MonitoringEvent connecting =
			new MonitoringEvent(this, MonitoringSeverity.OK)
			.forgetWhenOccurred(this.reconnect)
			.afterTimeout(
					45000L,
					this.connectionTimeout,
					"Connection timeout");
	private final MonitoringEvent connectionLost =
			new MonitoringEvent(this, MonitoringSeverity.ERROR);
	private final MonitoringEvent connected =
			new MonitoringEvent(this, MonitoringSeverity.OK)
			.forgetWhenOccurred(this.connecting)
			.forgetWhenOccurred(this.connectionLost);
	private final MonitoringEvent disconnected =
			new MonitoringEvent(this, MonitoringSeverity.WARNING)
			.forgetWhenOccurred(this.connecting);
	private final MonitoringEvent alreadyConnected =
			new MonitoringEvent(this, MonitoringSeverity.WARNING);
	private final MonitoringEvent shutdownTimeout =
			new MonitoringEvent(this, MonitoringSeverity.ERROR);
	private final MonitoringEvent shutdown =
			new MonitoringEvent(this, MonitoringSeverity.OK)
			.afterTimeout(10000, this.shutdownTimeout, "Shutdown timed out");

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
		getLogger().info(this + " Connecting");
		this.connected.occur("Connecting");
	}

	public void connected() {
		getLogger().info(this + " Connected");
		this.connected.occur("Connected");
	}

	public void alreadyConnected() {
		getLogger().warning(this + " Already connected");
		this.alreadyConnected.occur("Already connected");
	}

	public void connectionLost() {
		getLogger().error(this + " Connection lost");
		this.connectionLost.occur("Connection lost");
	}

	public void reconnect() {
		getLogger().info(this + " Reconnecting");
		this.reconnect.occur("Reconnecting");
	}

	public void shutdown() {
		getLogger().info(this + " Shutting down");
		this.shutdown.occur("Shutting down");
	}

	public void closingConnection() {
		getLogger().info(this + " Closing connection");
		this.shutdown.occur("Closing connection");
	}

	public void disconnected() {
		getLogger().info(this + " Disconnected");
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
