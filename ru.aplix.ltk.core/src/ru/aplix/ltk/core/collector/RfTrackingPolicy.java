package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


/**
 * RFID tracking policy.
 *
 * <p>It is used to provide trackers for {@link RfCollector RFID collectors}.
 * </p>
 *
 * <p>It also can be passed to the {@link RfSettings#setTrackingPolicy(
 * RfTrackingPolicy) RFID settings} to configure {@link RfConnection RFID
 * connections}.</p>
 */
public interface RfTrackingPolicy extends Parameterized {

	/**
	 * Default RFID tracking policy.
	 *
	 * <p>It provides trackers of type {@link DefaultRfTracker} with default
	 * settings.</p>
	 */
	RfTrackingPolicy DEFAULT_TRACKING_POLICY = new DefaultRfTrackingPolicy() {
		@Override
		public void read(Parameters params) {
		}
		@Override
		public void write(Parameters params) {
		}
	};

	/**
	 * Creates RFID tracker for the given collector.
	 *
	 * @param collector RFID collector to create tracker for.
	 *
	 * @return new RFID tracker instance.
	 */
	RfTracker newTracker(RfCollector collector);

}
