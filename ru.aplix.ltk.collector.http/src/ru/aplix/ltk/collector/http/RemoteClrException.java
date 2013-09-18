package ru.aplix.ltk.collector.http;


public class RemoteClrException extends RuntimeException {

	private static final long serialVersionUID = 54471956338415379L;

	private final String remoteExceptionClassName;
	private final String remoteStackTrace;

	public RemoteClrException(
			String exceptionClassName,
			String message,
			String stackTrace) {
		super(message);
		this.remoteExceptionClassName = exceptionClassName;
		this.remoteStackTrace = stackTrace;
	}

	public String getRemoteExceptionClassName() {
		return this.remoteExceptionClassName;
	}

	public String getRemoteStackTrace() {
		return this.remoteStackTrace;
	}

}
