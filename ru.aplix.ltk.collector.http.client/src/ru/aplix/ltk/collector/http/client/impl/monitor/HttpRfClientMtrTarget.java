package ru.aplix.ltk.collector.http.client.impl.monitor;

import ru.aplix.ltk.collector.http.client.impl.HttpRfConnection;
import ru.aplix.ltk.monitor.MonitoringTarget;


public class HttpRfClientMtrTarget extends MonitoringTarget<HttpRfClientMtr> {

	private final HttpRfConnection connection;

	public HttpRfClientMtrTarget(HttpRfConnection connection) {
		this.connection = connection;
	}

	public final HttpRfConnection getConnection() {
		return this.connection;
	}

	@Override
	public String toString() {
		if (this.connection == null) {
			return super.toString();
		}
		return this.connection.getSettings().getTargetId();
	}

	@Override
	protected HttpRfClientMtr newMonitoring() {
		return new HttpRfClientMtr();
	}

}
