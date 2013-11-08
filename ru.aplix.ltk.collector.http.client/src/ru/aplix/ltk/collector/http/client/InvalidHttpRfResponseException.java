package ru.aplix.ltk.collector.http.client;


/**
 * An exception, which indicates invalid HTTP server response.
 *
 * <p>This means that the URL requested is probably not a RFID HTTP server's
 * one.</p>
 */
public class InvalidHttpRfResponseException extends RuntimeException {

	private static final long serialVersionUID = -4077558429766154510L;

	public InvalidHttpRfResponseException() {
	}

	public InvalidHttpRfResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidHttpRfResponseException(String message) {
		super(message);
	}

	public InvalidHttpRfResponseException(Throwable cause) {
		super(cause);
	}

}
