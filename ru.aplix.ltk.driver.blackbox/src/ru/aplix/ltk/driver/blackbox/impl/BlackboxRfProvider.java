package ru.aplix.ltk.driver.blackbox.impl;

import java.util.LinkedList;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.collector.RfTracking;
import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.driver.blackbox.BlackboxRfSettings;


class BlackboxRfProvider
		implements RfProvider<BlackboxRfSettings>, RfTracker {

	private final RfCollector collector =
			new RfCollector(RfSource.EMPTY_RF_SOURCE, this);
	private final LinkedList<RfTracker> trackers = new LinkedList<>();
	private RfTracking tracking;
	private boolean trackingStarted;

	@Override
	public String getId() {
		return "blackbox";
	}

	@Override
	public String getName() {
		return "Blackbox RFID Driver";
	}

	@Override
	public Class<? extends BlackboxRfSettings> getSettingsType() {
		return BlackboxRfSettings.class;
	}

	@Override
	public BlackboxRfSettings newSettings() {
		return new BlackboxRfSettings();
	}

	@Override
	public BlackboxRfSettings copySettings(BlackboxRfSettings settings) {
		return settings.clone();
	}

	@Override
	public RfConnection connect(BlackboxRfSettings settings) {

		final RfTracker tracker = settings.getTracker();

		if (tracker != null) {
			addTracker(tracker);
		}

		return new BlackboxRfConnection(this.collector, settings);
	}

	@Override
	public synchronized void initRfTracker(RfTracking tracking) {
		this.tracking = tracking;
		for (RfTracker tracker : this.trackers) {
			tracker.initRfTracker(tracking);
		}
	}

	@Override
	public synchronized void startRfTracking() {
		this.trackingStarted = true;
		for (RfTracker tracker : this.trackers) {
			tracker.startRfTracking();
		}
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {
	}

	@Override
	public synchronized void stopRfTracking() {
		this.trackingStarted = false;
		for (RfTracker tracker : this.trackers) {
			tracker.startRfTracking();
		}
	}

	private synchronized void addTracker(RfTracker tracker) {
		this.trackers.add(tracker);
		if (this.tracking != null) {
			tracker.initRfTracker(this.tracking);
			if (this.trackingStarted) {
				tracker.startRfTracking();
			}
		}
	}

}
