package ru.aplix.ltk.store.web.blackbox;

import org.junit.runner.JUnitCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.web.blackbox.test.DummyTest;
import ru.aplix.ltk.store.web.blackbox.test.StatusTest;


@Component("blackboxRunner")
public class BlackboxRunner {

	private static BlackboxRunner instance;

	public static BlackboxRunner getInstance() {
		return instance;
	}

	@Autowired
	private RfStore rfStore;

	@Autowired
	@Qualifier("httpRfProvider")
	private RfProvider<HttpRfSettings> rfProvider;

	public BlackboxRunner() {
		instance = this;
	}

	public final RfStore getRfStore() {
		return this.rfStore;
	}

	public final RfProvider<HttpRfSettings> getRfProvider() {
		return this.rfProvider;
	}

	public TestResults run() {

		final TestResults result = new TestResults();
		final JUnitCore junit = new JUnitCore();

		junit.addListener(result);
		junit.run(DummyTest.class, StatusTest.class);

		return result;
	}

}
