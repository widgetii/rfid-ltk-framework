package ru.aplix.ltk.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


public class MonitoringTracker<M extends Monitoring>
		extends ServiceTracker<Monitor, M> {

	private final MonitoringTarget<M> target;
	private final AtomicBoolean monitored = new AtomicBoolean();

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

}
