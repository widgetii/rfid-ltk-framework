package ru.aplix.ltk.monitor;


/**
 * Monitoring report severity.
 */
public enum MonitoringSeverity {

	/**
	 * Everything operates as expected.
	 */
	OK,

	/**
	 * Everything operates as expected, but there are things to remember.
	 *
	 * <p>This can be used as a reminder e.g. when some service is manually
	 * stopped.</p>
	 */
	REMINDER,

	/**
	 * Warning.
	 *
	 * <p>This can be used e.g. to inform that some errors happened in past,
	 * but it is ok now.</p>
	 */
	WARNING,

	/**
	 * Error.
	 *
	 * <p>This can be used to report that some error happened just now. Later,
	 * after the service was recovered from this error, a {@link #WARNING
	 * warning} can be reported instead of error. But if the service can not
	 * recover from this error, a {@link #FATAL fatal error} should be reported
	 * instead.</p>
	 */
	ERROR,

	/**
	 * Fatal error.
	 *
	 * <p>This should be used to report unrecoverable, fatal errors.</p>
	 */
	FATAL

}
