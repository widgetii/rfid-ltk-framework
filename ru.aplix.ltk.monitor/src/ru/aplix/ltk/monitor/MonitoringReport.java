package ru.aplix.ltk.monitor;

import static java.util.Objects.requireNonNull;


/**
 * Monitoring report.
 *
 * <p>Contains information of some monitoring aspect. Can be constructed by one
 * of {@link MonitoringReports} methods.</p>
 */
public final class MonitoringReport {

	private final MonitoringTarget<?> target;
	private final MonitoringSeverity severity;
	private final String message;
	private final Throwable cause;

	MonitoringReport(
			MonitoringTarget<?> target,
			MonitoringSeverity severity,
			String message,
			Throwable cause) {
		requireNonNull(severity, "Monitoring report severity not specified");
		this.target = target;
		this.severity = severity;
		this.message = message;
		this.cause = cause;
	}

	/**
	 * Monitoring target.
	 *
	 * @return monitoring target this report is about.
	 */
	public final MonitoringTarget<?> getTarget() {
		return this.target;
	}

	/**
	 * Report severity.
	 *
	 * @return severity.
	 */
	public final MonitoringSeverity getSeverity() {
		return this.severity;
	}

	/**
	 * Report message.
	 *
	 * @return message string, or <code>null</code>
	 */
	public final String getMessage() {
		return this.message;
	}

	/**
	 * Error cause.
	 *
	 * @return a throwable causes this error, or <code>null</code>.
	 */
	public final Throwable getCause() {
		return this.cause;
	}

	@Override
	public String toString() {

		final StringBuilder out = new StringBuilder();

		out.append(this.target).append("[").append(this.severity);

		if (this.message == null && this.cause == null) {
			out.append(']');
			return out.toString();
		}

		out.append("]: ");
		if (this.message != null) {
			out.append(this.message);
			if (this.cause != null) {
				out.append(" (Cause: ").append(this.cause).append(')');
			}
		} else {
			out.append(this.cause);
		}

		return out.toString();
	}
}
