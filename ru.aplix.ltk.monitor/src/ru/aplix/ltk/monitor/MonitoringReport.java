package ru.aplix.ltk.monitor;

import static java.util.Objects.requireNonNull;

import java.util.TreeSet;


/**
 * Monitoring report.
 *
 * <p>An instance of this class can be passed to
 * {@link Monitoring#report(MonitoringReport)} method to build a report for it.
 */
public abstract class MonitoringReport {

	private static final long ONE_DAY = 24 * 60 * 60 * 1000L;

	private final TreeSet<ReportLine> lines = new TreeSet<>();
	private MonitoringTarget<?> target;
	private long since;
	private MonitoringSeverity minSeverity = MonitoringSeverity.REMINDER;

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
	 * The minimum severity of events to report.
	 *
	 * @return minimum severity, {@link MonitoringSeverity#REMINDER} by default.
	 */
	public MonitoringSeverity getMinSeverity() {
		return this.minSeverity;
	}

	/**
	 * Sets the minimum severity of events to report.
	 *
	 * @param minSeverity new minimum severity, or <code>null</code> to set it
	 * to the {@link MonitoringSeverity#REMINDER}.
	 */
	public void setMinSeverity(MonitoringSeverity minSeverity) {
		this.minSeverity =
				minSeverity != null ? minSeverity : MonitoringSeverity.REMINDER;
	}

	/**
	 * Appends monitoring report.
	 *
	 * @param timestamp UNIX event time in milliseconds.
	 * @param eventId event identifier.
	 * @param severity report severity.
	 * @param message report message.
	 * @param cause the cause of error.
	 */
	public final void report(
			long timestamp,
			long eventId,
			MonitoringSeverity severity,
			String message,
			Throwable cause) {
		requireNonNull(getTarget(), "Monitoring target not assigned");
		requireNonNull(severity, "Monitoring severity not specified");
		if (severity.ordinal() < getMinSeverity().ordinal()) {
			return;
		}
		if (!severity.ignoresTime() && getSince() > timestamp) {
			return;
		}
		this.lines.add(new ReportLine(
				timestamp,
				eventId,
				severity,
				message,
				cause));
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
	 * Ends the reporting of monitoring target. Resumes the reporting of the
	 * previous one.
	 *
	 * @param previousTarget previous monitoring target.
	 */
	protected void endReporting(MonitoringTarget<?> previousTarget) {
		this.target = previousTarget;
	}

	final void printLines() {
		for (ReportLine line : this.lines) {
			addReport(line.timestamp, line.severity, line.message, line.cause);
		}
		this.lines.clear();
	}

	private final static class ReportLine implements Comparable<ReportLine> {

		private final long timestamp;
		private final long eventId;
		private final MonitoringSeverity severity;
		private final String message;
		private final Throwable cause;

		ReportLine(
				long timestamp,
				long eventId,
				MonitoringSeverity severity,
				String message,
				Throwable cause) {
			this.timestamp = timestamp;
			this.eventId = eventId;
			this.severity = severity;
			this.message = message;
			this.cause = cause;
		}

		@Override
		public int compareTo(ReportLine o) {

			final long tdiff = this.timestamp - o.timestamp;

			if (tdiff > 0) {
				return 1;
			}
			if (tdiff < 0) {
				return -1;
			}

			final long ediff = this.eventId - o.eventId;

			if (ediff > 0) {
				return 1;
			}
			if (ediff < 0) {
				return -1;
			}

			return 0;
		}

		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;

			result =
					prime * result
					+ (int) (this.eventId ^ (this.eventId >>> 32));
			result =
					prime * result
					+ (int) (this.timestamp ^ (this.timestamp >>> 32));

			return result;
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

			final ReportLine other = (ReportLine) obj;

			if (this.eventId != other.eventId) {
				return false;
			}
			if (this.timestamp != other.timestamp) {
				return false;
			}

			return true;
		}

	}

}
