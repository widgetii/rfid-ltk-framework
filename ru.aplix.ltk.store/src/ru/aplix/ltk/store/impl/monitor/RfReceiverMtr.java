package ru.aplix.ltk.store.impl.monitor;

import ru.aplix.ltk.monitor.Monitoring;
import ru.aplix.ltk.monitor.MonitoringEvent;
import ru.aplix.ltk.store.RfReceiver;


public class RfReceiverMtr extends Monitoring {

	private final MonitoringEvent starting =
			trace("starting")
			.afterTimeoutOccur(
					10000L,
					error(),
					"Starting for too long. Failed?");
	private final MonitoringEvent started =
			info("started")
			.forgetOnUpdate(this.starting);
	private final MonitoringEvent stopping =
			info("stopping")
			.afterTimeoutOccur(
					10000L,
					error(),
					"Stopping for too long. Failed?");
	private final MonitoringEvent stopped =
			reminder("stopped")
			.forgetOnUpdate(this.stopping);
	private final MonitoringEvent unexpectedError =
			fatal("unexpected error");
	private final MonitoringEvent updateReceiver =
			trace("update receiver");
	private final MonitoringEvent delete =
			info("delete");
	private final MonitoringEvent shutdown =
			trace("shutdown");

	{
		this.started.forgetOnUpdate(this.stopped);
	}

	public final RfReceiver<?> getRfReceiver() {

		final RfReceiverMtrTarget target = (RfReceiverMtrTarget) getTarget();

		return target != null ? target.getRfReceiver() : null;
	}

	public void starting() {
		this.starting.occur("Starting");
	}

	public void started() {
		this.started.occur("Started");
	}

	public void stopping() {
		this.stopping.occur("Stop");
	}

	public void stopped() {
		this.stopped.occur("Stopped");
	}

	public void update() {
		this.updateReceiver.occur("Update");
	}

	public void delete(boolean deleteTags) {
		this.delete.occur(
				deleteTags ? "Delete with tags" : "Delete without tags");
	}

	public void shutdown() {
		this.shutdown.occur("Shutdown");
	}

	public void unexpectedError(String message, Throwable e) {
		this.unexpectedError.occur(message, e);
	}

	@Override
	protected void init() {
	}

}
