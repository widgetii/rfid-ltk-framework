package ru.aplix.ltk.store.web.blackbox;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class TestResults extends RunListener implements Iterable<TestResult> {

	private final LinkedHashMap<Description, TestResult> results =
			new LinkedHashMap<>();
	private Result result;

	public final Result getResult() {
		return this.result;
	}

	@Override
	public void testFinished(Description description) throws Exception {

		final TestResult existing =
				this.results.put(description, TestResult.testRan(description));

		if (existing != null) {
			this.results.put(description, existing);
		}
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		this.results.put(
				failure.getDescription(),
				TestResult.testFailed(failure));
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		this.results.put(
				failure.getDescription(),
				TestResult.testAssumptionFailed(failure));
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		this.results.put(description, TestResult.testIgnored(description));
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		this.result = result;
	}

	@Override
	public Iterator<TestResult> iterator() {
		return this.results.values().iterator();
	}

	public void print(Appendable out) throws IOException {

		boolean nl = false;

		for (TestResult result : this) {
			if (nl) {
				out.append("\n");
			} else {
				nl = true;
			}
			result.print(out);
		}
	}

	@Override
	public String toString() {
		if (this.results == null) {
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
