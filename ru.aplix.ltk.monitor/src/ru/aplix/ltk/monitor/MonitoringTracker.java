package ru.aplix.ltk.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.monitor.impl.MonitorImpl;


public class MonitoringTracker<M extends Monitoring>
		extends ServiceTracker<Monitor, M> {

	private final MonitoringTarget<M> target;
	private final AtomicBoolean monitored = new AtomicBoolean();
	private MonitorImpl backupMonitor;

	public MonitoringTracker(
			BundleContext context,
			MonitoringTarget<M> target) {
		super(context, Monitor.class, null);
		this.target = target;
	}

	public MonitoringTarget<M> getTarget() {
		return this.target;
	}

	@Override
	public M getService() {

		final M service = super.getService();
		final MonitorImpl backupMonitor;

		synchronized (this) {
			if (service != null) {
				closeBackupMonitor();
				return service;
			}
			this.backupMonitor = backupMonitor = new MonitorImpl();
			this.backupMonitor.backup(this.context);
		}

		return backupMonitor.monitoringOf(getTarget());
	}

	@Override
	public void close() {
		try {
			super.close();
		} finally {
			closeBackupMonitor();
		}
	}

	@Override
	public M addingService(ServiceReference<Monitor> reference) {
		if (!this.monitored.compareAndSet(false, true)) {
			return null;
		}

		final Monitor monitor = this.context.getService(reference);

		return monitor.monitoringOf(getTarget());
	}

	@Override
	public void removedService(
			ServiceReference<Monitor> reference,
			M service) {
		if (this.monitored.compareAndSet(true, false)) {
			this.context.ungetService(reference);
		}
	}

	private synchronized void closeBackupMonitor() {
		this.backupMonitor = null;
	}

}
