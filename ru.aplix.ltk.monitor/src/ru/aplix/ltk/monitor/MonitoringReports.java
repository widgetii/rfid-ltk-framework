package ru.aplix.ltk.monitor;

import static java.util.Objects.requireNonNull;


/**
 * Monitoring reports collector.
 *
 * <p>An instance of this class can be passed to
 * {@link Monitoring#report(MonitoringReports)} method to build reports for it.
 */
public abstract class MonitoringReports {

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
	 *
	 * @return appended monitoring report instance.
	 */
	public final MonitoringReport report(
			MonitoringSeverity severity,
			String message) {
		requireNonNull(getTarget(), "Unknown monitoring target to report");

		final MonitoringReport report =
				new MonitoringReport(getTarget(), severity, message, null);

		addReport(report);

		return report;
	}

	/**
	 * Appends monitoring error report.
	 *
	 * @param severity report severity.
	 * @param message report message.
	 * @param cause the cause of error.
	 *
	 * @return appended monitoring report instance.
	 */
	public final MonitoringReport reportError(
			MonitoringSeverity severity,
			String message,
			Throwable cause) {
		requireNonNull(getTarget(), "Unknown monitoring target to report");

		final MonitoringReport report =
				new MonitoringReport(getTarget(), severity, message, cause);

		addReport(report);

		return report;
	}

	/**
	 * Appends monitoring error report.
	 *
	 * @param severity report severity.
	 * @param cause the cause of error.
	 *
	 * @return appended monitoring report instance.
	 */
	public final MonitoringReport reportError(
			MonitoringSeverity severity,
			Throwable cause) {
		return reportError(severity, cause.getMessage(), cause);
	}

	/**
	 * Adds the report
	 *
	 * <p>This method is called by reporting methods to add the constructed
	 * reports to this reports collection.</p>
	 *
	 * @param report report to add.
	 */
	protected abstract void addReport(MonitoringReport report);

	final MonitoringTarget<?> startReporting(MonitoringTarget<?> target) {

		final MonitoringTarget<?> previousTarget = this.target;

		this.target = target;

		return previousTarget;
	}

	final void stopReporting(MonitoringTarget<?> previousTarget) {
		this.target = previousTarget;
	}

}
