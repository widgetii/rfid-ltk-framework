package ru.aplix.ltk.driver.blackbox.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.source.RfDataReceiver;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.core.source.RfStatusUpdater;
import ru.aplix.ltk.driver.blackbox.BlackboxRfSettings;


final class BlackboxRfConnection extends RfConnection implements RfSource {

	private final RfTracker tracker;

	public BlackboxRfConnection(BlackboxRfSettings settings) {
		super(settings);
		this.tracker = settings.getTracker();
		if (this.tracker == null) {
			throw new IllegalArgumentException(
					"Blackbox RFID tracker not specified");
		}
	}

	@Override
	protected RfSource createSource() {
		return this;
	}

	@Override
	public void requestRfStatus(RfStatusUpdater updater) {
	}

	@Override
	public void requestRfData(RfDataReceiver receiver) {
	}

	@Override
	public void rejectRfData() {
	}

	@Override
	public void rejectRfStatus() {
	}

}
