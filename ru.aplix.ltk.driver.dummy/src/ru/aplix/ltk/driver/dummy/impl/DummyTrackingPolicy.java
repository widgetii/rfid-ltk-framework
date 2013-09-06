package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.collector.*;
import ru.aplix.ltk.core.util.HttpParams;
import ru.aplix.ltk.driver.dummy.DummyRfSettings;


public class DummyTrackingPolicy implements RfTrackingPolicy {

	private final DummyRfSettings settings;

	public DummyTrackingPolicy(DummyRfSettings settings) {
		this.settings = settings;
	}

	@Override
	public RfTracker newTracker(RfCollector collector) {

		final DefaultRfTracker tracker = new DefaultRfTracker();
		final long readPeriod = this.settings.getReadPeriod();

		tracker.setInvalidationTimeout(readPeriod + (readPeriod >> 1));
		tracker.setTransactionTimeout(this.settings.getGenerationPeriod());

		return tracker;
	}

	@Override
	public void httpDecode(HttpParams params) {
	}

	@Override
	public void httpEncode(HttpParams params) {
	}

}
