package ru.aplix.ltk.driver.blackbox;

import static ru.aplix.ltk.core.collector.RfTrackingPolicy.NO_TRACKING_POLICY;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.util.Parameters;


/**
 * RFID settings for black box testing.
 *
 * <p>Tracking policy setting is ignored. Instead, an RFID tracker should be
 * {@link #setTracker(RfTracker) provided}. The {@link ManualRfTracker} is a
 * most appropriate implementation for black box testing.</p>
 *
 * <p>The black box driver does not generate any RFID data by itself. Instead it
 * relies on user in this.</p>
 */
public class BlackboxRfSettings implements RfSettings, Cloneable {

	private TrackerPolicy trackerPolicy;

	@Override
	public String getTargetId() {
		return "";
	}

	/**
	 * RFID tracker instance to use for black box testing.
	 *
	 * @return tracker instance, or <code>null</code> if not set yet.
	 */
	public RfTracker getTracker() {
		return this.trackerPolicy != null ? this.trackerPolicy.tracker : null;
	}

	/**
	 * Sets RFID tracker to use for black box testing.
	 *
	 * <p>This method also updates the tracking policy.</p>
	 *
	 * @param tracker new tracker instance, or <code>null</code> set it to
	 * {@link RfTracker#NO_TRACKING no-tracking} one.
	 */
	public void setTracker(RfTracker tracker) {
		this.trackerPolicy =
				tracker == null ? null : new TrackerPolicy(tracker);
	}

	@Override
	public RfTrackingPolicy getTrackingPolicy() {
		return this.trackerPolicy != null
				? this.trackerPolicy : NO_TRACKING_POLICY;
	}

	@Override
	public void setTrackingPolicy(RfTrackingPolicy trackingPolicy) {
	}

	@Override
	public void read(Parameters params) {
	}

	@Override
	public void write(Parameters params) {
	}

	@Override
	public BlackboxRfSettings clone() {
		try {
			return (BlackboxRfSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	private static final class TrackerPolicy
			implements RfTrackingPolicy {

		private final RfTracker tracker;

		TrackerPolicy(RfTracker tracker) {
			this.tracker = tracker;
		}

		@Override
		public void read(Parameters params) {
		}

		@Override
		public void write(Parameters params) {
		}

		@Override
		public RfTracker newTracker(RfCollector collector) {
			return this.tracker;
		}

		@Override
		public String toString() {
			if (this.tracker == null) {
				return super.toString();
			}
			return this.tracker.toString();
		}

	}

}
