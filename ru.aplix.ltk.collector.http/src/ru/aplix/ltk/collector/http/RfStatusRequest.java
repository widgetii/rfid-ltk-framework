package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.core.source.RfStatus.RF_ERROR;

import java.io.PrintWriter;
import java.io.StringWriter;

import ru.aplix.ltk.core.source.RfStatus;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class RfStatusRequest implements RfStatusMessage, Parameterized {

	private ClrClientId clientId;
	private String rfReaderId;
	private RfStatus rfStatus = RF_ERROR;
	private String errorMessage;
	private Throwable cause;

	public RfStatusRequest(ClrClientId clientId, RfStatusMessage status) {
		this.clientId = clientId;
		this.rfReaderId = status.getRfReaderId();
		this.rfStatus = status.getRfStatus();
		this.errorMessage = status.getErrorMessage();
		this.cause = status.getCause();
	}

	public RfStatusRequest(Parameters parameters) {
		read(parameters);
	}

	@Override
	public String getRfReaderId() {
		return this.rfReaderId;
	}

	@Override
	public RfStatus getRfStatus() {
		return this.rfStatus;
	}

	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public Throwable getCause() {
		return this.cause;
	}

	@Override
	public void read(Parameters params) {
		this.rfReaderId =
				params.valueOf("rfReaderId", null, getRfReaderId());
		this.rfStatus = params.enumValueOf(
				RfStatus.class,
				"rfStatus",
				RF_ERROR,
				getRfStatus());
		this.errorMessage =
				params.valueOf("errorMessage", null, getErrorMessage());
		this.cause = readCause(params);
	}

	@Override
	public void write(Parameters params) {
		params.set("client", this.clientId.getUUID().toString())
		.set("type", "status")
		.set("rfReaderId", getRfReaderId())
		.set("rfStatus", getRfStatus())
		.set("errorMessage", getErrorMessage());
		writeCause(params);
	}

	private Throwable readCause(Parameters params) {

		final Parameters causeParams = params.sub("cause");
		final String causeClass = causeParams.valueOf("");

		if (causeClass == null) {
			return getCause();
		}

		return new RemoteClrException(
				causeClass,
				causeParams.valueOf("message"),
				causeParams.valueOf("stackTrace"));
	}

	private void writeCause(Parameters params) {

		final Throwable cause = getCause();

		if (cause == null) {
			return;
		}

		final Parameters causeParams = params.sub("cause");

		causeParams.set("", cause.getClass().getName());
		causeParams.set("message", cause.getMessage());
		causeParams.set("stackTrace", stackTrace(cause));
	}

	private String stackTrace(Throwable cause) {

		final StringWriter str = new StringWriter();

		try (PrintWriter out = new PrintWriter(str)) {
			cause.printStackTrace(out);
		}

		return str.toString();
	}

}
