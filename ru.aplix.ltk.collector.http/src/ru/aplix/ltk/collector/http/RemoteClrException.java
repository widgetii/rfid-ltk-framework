package ru.aplix.ltk.collector.http;


/**
 * An exception occurred remotely at HTTP collector.
 *
 * <p>An instance of this class is constructed instead of original exception
 * instance when reading the {@link RfStatusRequest#getCause() cause} of the
 * error occurred at remote HTTP collector.</p>
 *
 * <p>This class is necessary, as client may not contain the original exception
 * class.</p>
 */
public class RemoteClrException extends RuntimeException {

	private static final long serialVersionUID = 54471956338415379L;

	private final String remoteExceptionClassName;
	private final String remoteStackTrace;

	/**
	 * Constructs a remote HTTP collector exception.
	 *
	 * @param exceptionClassName remote exception class name.
	 * @param message remote exception message.
	 * @param stackTrace remote exception stack trace.
	 */
	public RemoteClrException(
			String exceptionClassName,
			String message,
			String stackTrace) {
		super(message);
		this.remoteExceptionClassName = exceptionClassName;
		this.remoteStackTrace = stackTrace;
	}

	/**
	 * Remote exception class name.
	 *
	 * @return the class name passed to the constructor.
	 */
	public String getRemoteExceptionClassName() {
		return this.remoteExceptionClassName;
	}

	/**
	 * Remote exception stack trace.
	 *
	 * @return the stack trace passed to the constructor.
	 */
	public String getRemoteStackTrace() {
		return this.remoteStackTrace;
	}

}
