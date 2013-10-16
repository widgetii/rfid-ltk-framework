package ru.aplix.ltk.store.impl.monitor;

import ru.aplix.ltk.monitor.Monitoring;
import ru.aplix.ltk.monitor.MonitoringEvent;


public class RfStoreMtr extends Monitoring {

	private final MonitoringEvent loadRecivers =
			trace("load receivers");
	private final MonitoringEvent failedToLoadReceivers =
			fatal("failed to load  receivers");
	private final MonitoringEvent createReceiver =
			trace("create receiver");
	private final MonitoringEvent shutdown =
			info("shutdown");
	private final MonitoringEvent unexpectedError =
			fatal("unexpected error");

	public void loadReceivers(String message) {
		this.loadRecivers.occur(message);
	}

	public void failedToLoadReceivers(String message, Throwable cause) {
		this.failedToLoadReceivers.occur(message, cause);
	}

	public void createReceiver() {
		this.createReceiver.occur("Create receiver");
	}

	public void shutdown() {
		this.shutdown.occur("Shutting down");
	}

	public void unexpectedError(String message, Throwable cause) {
		this.unexpectedError.occur(message, cause);
	}

	@Override
	protected void init() {
	}

}
