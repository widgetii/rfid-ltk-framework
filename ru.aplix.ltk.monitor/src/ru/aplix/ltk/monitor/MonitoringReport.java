package ru.aplix.ltk.monitor;

import static java.util.Objects.requireNonNull;


/**
 * Monitoring report.
 *
 * <p>An instance of this class can be passed to
 * {@link Monitoring#report(MonitoringReport)} method to build a report for it.
 */
public abstract class MonitoringReport {

	private static final long ONE_DAY = 24 * 60 * 60 * 1000L;

	private MonitoringTarget<?> target;
	private long since;

	public MonitoringReport() {
		this.since = System.currentTimeMillis() - ONE_DAY;
	}

	/**
	 * Monitoring target to build report for.
	 *
	 * @return monitoring target.
	 */
	public final MonitoringTarget<?> getTarget() {
		return this.target;
	}

	/**
	 * The time since which the report should be generated.
	 *
	 * <p>This time affects only warnings.</p>
	 *
	 * @return UNIX time in milliseconds. 24 hours ago by default.
	 */
	public long getSince() {
		return this.since;
	}

	/**
	 * Sets the time since which the report should be generated.
	 *
	 * @param since new UNIX time in milliseconds.
	 */
	public void setSince(long since) {
		this.since = since;
	}

	/**
	 * Appends monitoring report.
	 *
	 * @param timestamp UNIX event time in milliseconds.
	 * @param severity report severity.
	 * @param message report message.
	 */
	public void report(
			long timestamp,
			MonitoringSeverity severity,
			String message) {
		report(timestamp, severity, message, null);
	}

	/**
	 * Appends monitoring error report.
	 *
	 * @param timestamp UNIX event time in milliseconds.
	 * @param severity report severity.
	 * @param cause the cause of error.
	 */
	public void reportError(
			long timestamp,
			MonitoringSeverity severity,
			Throwable cause) {
		report(timestamp, severity, cause.getMessage(), cause);
	}

	/**
	 * Appends full monitoring report.
	 *
	 * @param timestamp UNIX event time in milliseconds.
	 * @param severity report severity.
	 * @param message report message.
	 * @param cause the cause of error.
	 */
	public final void report(
			long timestamp,
			MonitoringSeverity severity,
			String message,
			Throwable cause) {
		requireNonNull(getTarget(), "Monitoring target not assigned");
		requireNonNull(severity, "Monitoring severity not specified");
		if (severity.ignoresTime() || getSince() <= timestamp) {
			addReport(timestamp, severity, message, cause);
		}
	}

	/**
	 * Starts the reporting for the given monitoring target.
	 *
	 * @param target reported monitoring target.
	 *
	 * @return previously reported monitoring target.
	 */
	protected MonitoringTarget<?> startReporting(MonitoringTarget<?> target) {

		final MonitoringTarget<?> previousTarget = this.target;

		this.target = target;

		return previousTarget;
	}

	/**
	 * Appends monitoring report.
	 *
	 * <p>This method is called by reporting methods to add the constructed
	 * reports to this reports collection.</p>
	 *
	 * @param timestamp UNIX event time in milliseconds.
	 * @param severity report severity.
	 * @param message report message.
	 * @param cause the cause of error.
	 */
	protected abstract void addReport(
			long timestamp,
			MonitoringSeverity severity,
			String message,
			Throwable cause);

	/**
	 * Stops the reporting of monitoring target. Resumes the reporting of the
	 * previous one.
	 *
	 * @param previousTarget previous monitoring target.
	 */
	protected void stopReporting(MonitoringTarget<?> previousTarget) {
		this.target = previousTarget;
	}

}
