package ru.aplix.ltk.collector.http.client.impl.monitor;

import ru.aplix.ltk.collector.http.RemoteClrException;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.impl.HttpRfConnection;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.monitor.Monitoring;
import ru.aplix.ltk.monitor.MonitoringEvent;


public class HttpRfClientMtr extends Monitoring {

	private final MonitoringEvent reconnect =
			note("reconnect")
			.afterTimeout(
					45000L,
					error("reconnection timeout"),
					"Reconnection timeout");
	private final MonitoringEvent connecting =
			info("connecting")
			.forgetOnUpdate(this.reconnect)
			.afterTimeout(
					45000L,
					error("connection timeout"),
					"Connection timeout");
	private final MonitoringEvent error = error("error");
	private final MonitoringEvent status =
			trace("status")
			.forgetWhenOccurred(this.error);
	private final MonitoringEvent tag =
			trace("tag")
			.forgetWhenOccurred(this.error);
	private final MonitoringEvent ping =
			trace("ping")
			.forgetWhenOccurred(this.error);
	private final MonitoringEvent connectionLost = error("connection lost");
	private final MonitoringEvent connected =
			info("connected")
			.forgetWhenOccurred(this.connecting)
			.forgetWhenOccurred(this.connectionLost);
	private final MonitoringEvent disconnected = warning("disconnected");
	private final MonitoringEvent alreadyConnected =
			warning("already connected");
	private final MonitoringEvent shutdown =
			info("shutdown")
			.afterTimeout(
					10000,
					error("shutdown timeout"),
					"Shutdown timed out");

	HttpRfClientMtr() {
	}

	public final HttpRfConnection getConnection() {

		final HttpRfClientMtrTarget target =
				(HttpRfClientMtrTarget) getTarget();

		return target.getConnection();
	}

	public void statusRequested() {
		this.status.occur("Status requested");
	}

	public void statusRejected() {
		this.status.occur("Status rejected");
	}

	public void ping() {
		this.ping.occur("Ping received");
	}

	public void tag(RfTagAppearanceRequest tagAppearance) {
		this.tag.occur(
				"Tag appearance changed (" + tagAppearance.getRfTag()
				+ "): " + tagAppearance.getAppearance());
	}

	public void status(RfStatusMessage status) {
		if (!status.getRfStatus().isError()) {
			this.status.occur("Status update: " + status.getRfStatus());
			return;
		}

		final String errorMessage = status.getErrorMessage();
		final Throwable cause = status.getCause();

		if (cause instanceof RemoteClrException) {

			final RemoteClrException remote = (RemoteClrException) cause;

			this.error.occur(
					"Remote error: " + errorMessage
					+ ". Remote exception: "
					+ remote.getRemoteExceptionClassName()
					+ ": " + remote.getMessage());
			return;
		}

		this.error.occur(errorMessage, cause);
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
