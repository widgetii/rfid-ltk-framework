package ru.aplix.ltk.monitor;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Textual monitoring report.
 */
public class TextMonitoringReport extends MonitoringReport {

	private static final SimpleDateFormat TIMESTAMP_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

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

			final String title = target.toString();

			out().append(title).append('\n');
			for (int i = title.length(); i > 0; --i) {
				out().append('-');
			}
			out().append('\n');
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
			printReport(timestamp, severity, message, cause);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void endReporting(MonitoringTarget<?> previousTarget) {
		try {
			out().append('\n');
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		super.endReporting(previousTarget);
	}

	private void printReport(
			long timestamp,
			MonitoringSeverity severity,
			String message,
			Throwable cause)
	throws IOException {
		printAbbr(severity);
		out().append(' ').append(TIMESTAMP_FORMAT.format(new Date(timestamp)));
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
