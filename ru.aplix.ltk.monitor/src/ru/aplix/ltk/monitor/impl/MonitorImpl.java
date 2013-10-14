package ru.aplix.ltk.monitor.impl;

import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.monitor.*;
import ru.aplix.ltk.osgi.Logger;


public class MonitorImpl extends MonitoringContext implements Monitor {

	private Logger logger;
	private final HashMap<MonitoringTarget<?>, Monitoring<?>> monitorings =
			new HashMap<>();
	private ServiceRegistration<Monitor> registration;

	@Override
	public Monitor getMonitor() {
		return this;
	}

	@Override
	public Logger getLogger() {
		return this.logger;
	}

	@Override
	public <M extends Monitoring<?>> M monitoringOf(
			MonitoringTarget<M> target) {
		synchronized (this.monitorings) {

			@SuppressWarnings("unchecked")
			final M existing = (M) this.monitorings.get(target);

			if (existing != null) {
				return existing;
			}

			return createMonitoring(target);
		}
	}

	@Override
	public void report(MonitoringReports reports) {

		final Monitoring<?>[] monitorings;

		synchronized (this.monitorings) {
			monitorings = this.monitorings.values().toArray(
					new Monitoring<?>[this.monitorings.size()]);
		}

		for (Monitoring<?> monitoring : monitorings) {
			monitoring.report(reports);
		}
	}

	@Override
	protected void startMonitoring(Monitoring<?> monitoring) {
		this.monitorings.put(monitoring.getTarget(), monitoring);
	}

	@Override
	protected void stopMonitoring(Monitoring<?> monitoring) {
		synchronized (this.monitorings) {
			this.monitorings.remove(monitoring.getTarget());
		}
	}

	void register(BundleContext context) {
		this.logger = new Logger(context);
		this.logger.open();
		this.registration = context.registerService(Monitor.class, this, null);
	}

	void unregister() {
		try {
			this.registration.unregister();
		} finally {
			this.registration = null;
			this.logger.close();
		}
	}

}
