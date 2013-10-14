package ru.aplix.ltk.monitor;


/**
 * Monitoring report.
 *
 * <p>An instance of this class can be passed to
 * {@link Monitoring#report(MonitoringReport)} method to build a report for it.
 */
public abstract class MonitoringReport {

	private MonitoringTarget<?> target;

	/**
	 * Monitoring target to build report for.
	 *
	 * @return monitoring target.
	 */
	public final MonitoringTarget<?> getTarget() {
		return this.target;
	}

	/**
	 * Appends monitoring report.
	 *
	 * @param severity report severity.
	 * @param message report message.
	 */
	public void report(MonitoringSeverity severity, String message) {
		report(severity, message, null);
	}

	/**
	 * Appends monitoring error report.
	 *
	 * @param severity report severity.
	 * @param cause the cause of error.
	 */
	public void reportError(MonitoringSeverity severity, Throwable cause) {
		report(severity, cause.getMessage(), cause);
	}

	/**
	 * Appends monitoring report.
	 *
	 * <p>This method is called by reporting methods to add the constructed
	 * reports to this reports collection.</p>
	 *
	 * @param severity report severity.
	 * @param message report message.
	 * @param cause the cause of error.
	 */
	public abstract void report(
			MonitoringSeverity severity,
			String message,
			Throwable cause);

	final MonitoringTarget<?> startReporting(MonitoringTarget<?> target) {

		final MonitoringTarget<?> previousTarget = this.target;

		this.target = target;

		return previousTarget;
	}

	final void stopReporting(MonitoringTarget<?> previousTarget) {
		this.target = previousTarget;
	}

}
