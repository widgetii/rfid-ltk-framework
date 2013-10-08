package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.util.Parameters;


final class NoRfTracking implements RfTracker, RfTrackingPolicy {

	static final NoRfTracking INSTANCE = new NoRfTracking();

	private NoRfTracking() {
	}

	@Override
	public void initRfTracker(RfTracking tracking) {
	}

	@Override
	public void startRfTracking() {
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {
	}

	@Override
	public void stopRfTracking() {
	}

	@Override
	public RfTracker newTracker(RfCollector collector) {
		return this;
	}

	@Override
	public void read(Parameters params) {
	}

	@Override
	public void write(Parameters params) {
	}

	@Override
	public String toString() {
		return "No RFID Tracking";
	}

}
