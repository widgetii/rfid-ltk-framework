package ru.aplix.ltk.monitor;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Textual monitoring report.
 */
public class TextMonitoringReport extends MonitoringReport {

	private final Appendable out;

	/**
	 * Constructs a textual monitoring report.
	 *
	 * @param out an appendable to append the report to.
	 */
	public TextMonitoringReport(Appendable out) {
		requireNonNull(out, "A writer to print the report to is not specified");
		this.out = out;
	}

	/**
	 * An appendable to append the report to.
	 *
	 * @return the appendable passed to constructor.
	 */
	public final Appendable out() {
		return this.out;
	}

	@Override
	protected MonitoringTarget<?> startReporting(MonitoringTarget<?> target) {
		try {
			out().append(target.toString()).append('\n');
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return super.startReporting(target);
	}

	@Override
	protected void addReport(
			long timestamp,
			MonitoringSeverity severity,
			String message,
			Throwable cause) {
		try {
			printReport(severity, message, cause);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void printReport(
			MonitoringSeverity severity,
			String message,
			Throwable cause)
	throws IOException {
		printAbbr(severity);

		if (message != null || cause != null) {
			out().append(' ');
			if (message != null) {
				out().append(message);
				if (cause != null) {
					out().append('\n');
				}
			}
			if (cause != null) {
				printStackTrace(cause);
			}
		}

		out().append('\n');
	}

	private void printAbbr(MonitoringSeverity severity) throws IOException {

		final String abbr = severity.abbr();

		out().append('[').append(abbr);
		for (int i = abbr.length(); i < 5; ++i) {
			out().append(' ');
		}
		out().append(']');
	}

	private void printStackTrace(Throwable cause) throws IOException {

		final StringWriter st = new StringWriter();

		try (PrintWriter writer = new PrintWriter(st)) {
			cause.printStackTrace(writer);
		}

		out().append(st.getBuffer());
	}

}
