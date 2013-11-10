package ru.aplix.ltk.collector.http.client;

import ru.aplix.ltk.collector.http.ClrError;


/**
 * HTTP RFID server error.
 */
public class HttpRfServerException extends RuntimeException {

	private static final long serialVersionUID = 600607994820943458L;

	private final int statusCode;
	private final ClrError error;
	private final String[] errorArgs;

	public HttpRfServerException(
			String message,
			int statusCode,
			ClrError error,
			String... args) {
		super(message);
		this.statusCode = statusCode;
		this.error = error;
		if (args.length == 0 && error == ClrError.UNEXPECTED) {
			this.errorArgs = new String[] {message};
		} else {
			this.errorArgs = args;
		}
	}

	public final int getStatusCode() {
		return this.statusCode;
	}

	public final ClrError getError() {
		return this.error;
	}

	public final String[] getErrorArgs() {
		return this.errorArgs;
	}

}
