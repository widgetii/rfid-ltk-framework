package ru.aplix.ltk.monitor.impl;

import java.util.HashMap;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.monitor.*;
import ru.aplix.ltk.osgi.Logger;


public class MonitorImpl extends MonitoringContext implements Monitor {

	private Logger logger;
	private final HashMap<MonitoringTarget<?>, Monitoring> monitorings =
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
	public <M extends Monitoring> M monitoringOf(
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
	public void report(MonitoringReport report) {

		final TreeMap<TargetKey, Monitoring> monitorings = new TreeMap<>();

		synchronized (this.monitorings) {
			for (Monitoring monitoring : this.monitorings.values()) {
				monitorings.put(
						new TargetKey(monitoring.getTarget()),
						monitoring);
			}
		}

		for (Monitoring monitoring : monitorings.values()) {
			monitoring.report(report);
		}
	}

	@Override
	protected void startMonitoring(Monitoring monitoring) {
		this.monitorings.put(monitoring.getTarget(), monitoring);
	}

	@Override
	protected void stopMonitoring(Monitoring monitoring) {
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

	private static final class TargetKey implements Comparable<TargetKey> {

		private final MonitoringTarget<?> target;

		TargetKey(MonitoringTarget<?> target) {
			this.target = target;
		}

		@Override
		public int compareTo(TargetKey o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int hashCode() {
			return this.target.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			final TargetKey other = (TargetKey) obj;

			return this.target.equals(other.target);
		}

	}
}
