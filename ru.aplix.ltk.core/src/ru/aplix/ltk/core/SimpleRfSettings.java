package ru.aplix.ltk.core;

import static ru.aplix.ltk.core.collector.RfTrackingPolicy.DEFAULT_TRACKING_POLICY;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;


/**
 * Simple RFID settings implementation.
 *
 * <p>Uses the {@link RfTrackingPolicy#DEFAULT_TRACKING_POLICY default tracking
 * policy} if not overridden.</p>
 */
public class SimpleRfSettings implements RfSettings, Cloneable {

	private RfTrackingPolicy trackingPolicy = DEFAULT_TRACKING_POLICY;

	@Override
	public RfTrackingPolicy getTrackingPolicy() {
		return this.trackingPolicy;
	}

	@Override
	public void setTrackingPolicy(RfTrackingPolicy trackingPolicy) {
		this.trackingPolicy =
				trackingPolicy != null
				? trackingPolicy : DEFAULT_TRACKING_POLICY;
	}

	@Override
	public SimpleRfSettings clone() {
		try {
			return (SimpleRfSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

}
