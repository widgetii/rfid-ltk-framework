package ru.aplix.ltk.store.web.blackbox;

import org.junit.runner.JUnitCore;
import org.springframework.stereotype.Component;


@Component("blackboxRunner")
public class BlackboxRunner {

	public TestResults run() {

		final TestResults result = new TestResults();
		final JUnitCore junit = new JUnitCore();

		junit.addListener(result);
		junit.run(DummyTest.class);

		return result;
	}

}
