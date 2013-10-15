package ru.aplix.ltk.monitor;

import java.util.HashMap;

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
	TRACE("TRACE", LogService.LOG_DEBUG, false),

	/**
	 * Informational message.
	 *
	 * <p>Indicates normal operation.</p>
	 */
	INFO("INFO", LogService.LOG_INFO, false),

	/**
	 * Reminder.
	 *
	 * <p>Everything operates as expected, but there are things to remember.</p>
	 *
	 * <p>This can be used e.g. to remind that some service is manually
	 * stopped.</p>
	 */
	REMINDER("REM", LogService.LOG_INFO, true),

	/**
	 * Noticeable event.
	 *
	 * <p>There is no error yet, but something looks suspicious, or should be
	 * noticed.</p>
	 */
	NOTE("NOTE", LogService.LOG_INFO, false),

	/**
	 * Warning.
	 *
	 * <p>This can be used e.g. to inform that some errors happened in past,
	 * but it is ok now.</p>
	 */
	WARNING("WARN", LogService.LOG_WARNING, false),

	/**
	 * Error.
	 *
	 * <p>This can be used to report that some error happened just now. Later,
	 * after the service was recovered from this error, a {@link #WARNING
	 * warning} can be reported instead of error. But if the service can not
	 * recover from this error, a {@link #FATAL fatal error} should be reported
	 * instead.</p>
	 */
	ERROR("ERROR", LogService.LOG_ERROR, true),

	/**
	 * Fatal error.
	 *
	 * <p>This should be used to report unrecoverable, fatal errors.</p>
	 */
	FATAL("FATAL", LogService.LOG_ERROR, true);

	private final String abbr;
	private final boolean ignoresTime;
	private final int logLevel;

	MonitoringSeverity(String abbr, int logSeverity, boolean ignoresTime) {
		this.abbr = abbr;
		this.ignoresTime = ignoresTime;
		this.logLevel = logSeverity;
		Registry.byAbbr.put(abbr, this);
	}

	/**
	 * Restores monitoring severity by its abbreviation.
	 *
	 * @param abbr target {@link #abbr() abbreviation}.
	 *
	 * @return corresponding monitoring severity, or <code>null</code> if not
	 * found.
	 */
	public static MonitoringSeverity severityByAbbr(String abbr) {
		return Registry.byAbbr.get(abbr);
	}

	/**
	 * Severity abbreviation.
	 *
	 * @return an abbreviation string, containing up to 5 upper-case ASCII
	 * chars.
	 */
	public final String abbr() {
		return this.abbr;
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

	private static final class Registry {

		private static final HashMap<String, MonitoringSeverity> byAbbr =
				new HashMap<>(7);

	}

}
