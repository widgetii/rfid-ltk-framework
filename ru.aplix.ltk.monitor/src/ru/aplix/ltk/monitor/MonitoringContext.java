package ru.aplix.ltk.monitor;

import ru.aplix.ltk.osgi.Logger;


/**
 * Monitoring context.
 *
 * <p>An instance of this class is passed to {@link Monitoring#getContext()
 * monitoring instance} right before its {@link Monitoring#init()
 * initialization} during its {@link #createMonitoring(MonitoringTarget)
 * construction}.</p>
 */
public abstract class MonitoringContext {

	/**
	 * Monitoring service.
	 *
	 * @return monitoring service instance.
	 */
	public abstract Monitor getMonitor();

	/**
	 * Logger.
	 *
	 * @return logger to log events to.
	 */
	public abstract Logger getLogger();

	/**
	 * Creates monitoring of the given target.
	 *
	 * <p>This method calls {@link MonitoringTarget#newMonitoring()} method
	 * to create new monitoring instance.</p>
	 *
	 * <p>Monitoring service implementations should use this method to create
	 * and properly initialize the monitoring instances.<p>
	 *
	 * @param target monitoring target.
	 *
	 * @return monitoring instance.
	 */
	protected final <M extends Monitoring> M createMonitoring(
			MonitoringTarget<M> target) {

		final M monitoring = target.newMonitoring();

		monitoring.initMonitoring(this, target);

		return monitoring;
	}

	/**
	 * Starts monitoring.
	 *
	 * <p>This method is called during monitoring instance
	 * {@link #createMonitoring(MonitoringTarget) construction}, right before
	 * its {@link Monitoring#init() initialization}.</p>
	 *
	 * @param monitoring monitoring to start.
	 */
	protected abstract void startMonitoring(Monitoring monitoring);

	/**
	 * Stops monitoring.
	 *
	 * <p>This method is called when monitoring is {@link Monitoring#stop()
	 * stopped}.</p>
	 *
	 * @param monitoring monitoring to stop.
	 */
	protected abstract void stopMonitoring(Monitoring monitoring);

}
