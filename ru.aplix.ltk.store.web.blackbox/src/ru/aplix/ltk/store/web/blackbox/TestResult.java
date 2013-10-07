package ru.aplix.ltk.store.web.blackbox;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;


public final class TestResult {

	public static TestResult testRan(Description description) {
		requireNonNull(description, "Test description not specified");
		return new TestResult(description, null, false, false);
	}

	public static TestResult testIgnored(Description description) {
		requireNonNull(description, "Test description not specified");
		return new TestResult(description, null, false, true);
	}

	public static TestResult testFailed(Failure failure) {
		requireNonNull("Test failure not specified");
		return new TestResult(failure.getDescription(), failure, false, false);
	}

	public static TestResult testAssumptionFailed(Failure failure) {
		requireNonNull("Test assumption failure not specified");
		return new TestResult(failure.getDescription(), failure, true, false);
	}

	private final Description description;
	private final Failure failure;
	private final boolean assumptionFailure;
	private final boolean ignored;

	private TestResult(
			Description description,
			Failure failure,
			boolean assumptionFailure,
			boolean ignored) {
		this.description = description;
		this.failure = failure;
		this.assumptionFailure = assumptionFailure;
		this.ignored = ignored;
	}

	public final Description getDescription() {
		return this.description;
	}

	public final Failure getFailure() {
		return this.failure;
	}

	public final boolean isAssumptionFailure() {
		return this.assumptionFailure;
	}

	public final boolean isIgnored() {
		return this.ignored;
	}

	public void print(Appendable out) throws IOException {
		if (this.failure == null) {
			if (isIgnored()) {
				out.append("[Ignored] ");
			} else {
				out.append("[Ok] ");
			}
		} else if (!this.assumptionFailure) {
			out.append("[Failed] ");
		} else {
			out.append("[Assumption failed] ");
		}
		out.append(this.description.toString());
		if (this.failure != null) {
			out.append(": ");
			out.append(this.failure.toString());
		}
	}

	@Override
	public String toString() {
		if (this.description == null) {
			return super.toString();
		}

		final StringBuilder out = new StringBuilder();

		try {
			print(out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return out.toString();
	}

}
