package ru.aplix.ltk.collector.http.client;


/**
 * HTTP RFID server error.
 */
public class HttpRfServerException extends RuntimeException {

	private static final long serialVersionUID = 6617892844457773477L;

	private final int statusCode;
	private final String errorCode;
	private final String[] errorArgs;

	public HttpRfServerException(
			String message,
			int statusCode,
			String errorCode,
			String... args) {
		super(message);
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.errorArgs = args;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String[] getErrorArgs() {
		return this.errorArgs;
	}

}
