package ru.aplix.ltk.collector.http;

import java.io.PrintWriter;
import java.io.StringWriter;

import ru.aplix.ltk.core.util.ParameterType;
import ru.aplix.ltk.core.util.Parameters;


final class RemoteClrExceptionParameterType extends ParameterType<Throwable> {

	@Override
	public Throwable[] getValues(Parameters params, String name) {

		final Parameters causeParams = params.sub("cause");
		final String causeClass = causeParams.valueOf("");

		if (causeClass == null) {
			return null;
		}

		final RemoteClrException result = new RemoteClrException(
				causeClass,
				causeParams.valueOf("message"),
				causeParams.valueOf("stackTrace"));

		return new Throwable[] {result};
	}

	@Override
	public void setValues(Parameters params, String name, Throwable[] values) {
		if (values.length == 0) {
			return;
		}

		final Throwable cause = values[0];

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
