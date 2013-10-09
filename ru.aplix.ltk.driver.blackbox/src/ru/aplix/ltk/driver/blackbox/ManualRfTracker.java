package ru.aplix.ltk.driver.blackbox;

import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.collector.RfTracking;
import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.source.RfStatusUpdater;


/**
 * Manual RFID tracker.
 *
 * <p>An instance of this class can be passed to
 * {@link BlackboxRfSettings#setTracker(RfTracker) black box RFIS settings} for
 * the new connection. Such instance can be used to produce RFID events directly
 * from user code.</p>
 */
public class ManualRfTracker implements RfTracker, RfStatusUpdater {

	private RfTracking tracking;
	private boolean trackingStarted;

	/**
	 * Whether an RFID tracking is started.
	 *
	 * @return <code>true</code> when {@link #startRfTracking()} is already
	 * called, but {@link #stopRfTracking()} is not.
	 */
	public boolean isTrackingStarted() {
		return this.trackingStarted;
	}

	@Override
	public void initRfTracker(RfTracking tracking) {
		this.tracking = tracking;
	}

	@Override
	public void startRfTracking() {
		this.trackingStarted = true;
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {
	}

	@Override
	public void stopRfTracking() {
		this.trackingStarted = false;
	}

	@Override
	public final void updateStatus(RfStatusMessage status) {
		if (this.tracking != null) {
			this.tracking.updateStatus(status);
		}
	}

	/**
	 * Reports a change in RFID tag appearance.
	 *
	 * @param tagAppearance tag appearance change to report.
	 */
	public final void updateTagAppearance(
			RfTagAppearanceMessage tagAppearance) {
		if (this.tracking != null) {
			this.tracking.updateTagAppearance(tagAppearance);
		}
	}

}
