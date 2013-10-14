package ru.aplix.ltk.monitor;


/**
 * Monitoring service.
 *
 * <p>This OSGi service can be used to monitor different services and
 * sub-systems called {@link MonitoringTarget monitoring targets}.</p>
 *
 * <p>Each monitoring target can report events specific to it to dedicated
 * {@link Monitoring} instance, the same way it reports them to the log.
 * Such events will be reported to {@link MonitoringListener monitoring
 * listeners}, which can, for example, generate monitoring reports.</p>
 *
 * <p>This service is thread-safe.</p>
 */
public interface Monitor {

	/**
	 * Returns monitoring of the given target.
	 *
	 * <p>Creates monitoring instance if not created yet. At most one monitoring
	 * instance can be created for the given target.</p>
	 *
	 * @param target monitoring target.
	 *
	 * @return monitoring instance of the given target.
	 */
	<M extends Monitoring<?>> M monitoringOf(MonitoringTarget<M> target);

	/**
	 * Build monitoring reports for all known monitoring targets.
	 *
	 * <p>This method calls {@link Monitoring#report(MonitoringReports)} methods
	 * for each created and not {@link Monitoring#report(MonitoringReports)
	 * stopped yet} monitoring instance.</p>
	 *
	 * @param reports reports collection to add reports to.
	 */
	void report(MonitoringReports reports);

}
