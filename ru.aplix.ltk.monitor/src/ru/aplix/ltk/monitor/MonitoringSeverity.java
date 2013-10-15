package ru.aplix.ltk.monitor;

import org.osgi.service.log.LogService;


/**
 * Monitoring report severity.
 */
public enum MonitoringSeverity {

	/**
	 * A trace of some event.
	 *
	 * <p>Mainly used for debugging purposes.</p>
	 */
	TRACE(false, LogService.LOG_DEBUG),

	/**
	 * Informational message.
	 *
	 * <p>Indicates normal operation.</p>
	 */
	INFO(false, LogService.LOG_INFO),

	/**
	 * Reminder.
	 *
	 * <p>Everything operates as expected, but there are things to remember.</p>
	 *
	 * <p>This can be used e.g. to remind that some service is manually
	 * stopped.</p>
	 */
	REMINDER(true, LogService.LOG_INFO),

	/**
	 * Noticeable event.
	 *
	 * <p>There is no error yet, but something looks suspicious, or should be
	 * noticed.</p>
	 */
	NOTE(false, LogService.LOG_INFO),

	/**
	 * Warning.
	 *
	 * <p>This can be used e.g. to inform that some errors happened in past,
	 * but it is ok now.</p>
	 */
	WARNING(false, LogService.LOG_WARNING),

	/**
	 * Error.
	 *
	 * <p>This can be used to report that some error happened just now. Later,
	 * after the service was recovered from this error, a {@link #WARNING
	 * warning} can be reported instead of error. But if the service can not
	 * recover from this error, a {@link #FATAL fatal error} should be reported
	 * instead.</p>
	 */
	ERROR(true, LogService.LOG_ERROR),

	/**
	 * Fatal error.
	 *
	 * <p>This should be used to report unrecoverable, fatal errors.</p>
	 */
	FATAL(true, LogService.LOG_ERROR);

	private final boolean ignoresTime;
	private final int logLevel;

	MonitoringSeverity(boolean ignoresTime, int logSeverity) {
		this.ignoresTime = ignoresTime;
		this.logLevel = logSeverity;
	}

	/**
	 * Whether events with this severity reported despite the
	 * {@link MonitoringReport#getSince() report timings}.
	 *
	 * @return <code>true</code> if events will be reported anyway, or
	 * <code>false</code> if report timings will be respected.
	 */
	public final boolean ignoresTime() {
		return this.ignoresTime;
	}

	/**
	 * The level of logged message.
	 *
	 * @return {@code LogService} level.
	 */
	public final int getLogLevel() {
		return this.logLevel;
	}

}
