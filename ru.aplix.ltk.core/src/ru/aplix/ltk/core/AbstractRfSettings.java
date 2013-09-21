package ru.aplix.ltk.core;

import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.util.Parameters;


/**
 * Abstract RFID settings implementation.
 *
 * <p>Uses the {@link RfTrackingPolicy#DEFAULT_TRACKING_POLICY default tracking
 * policy} if not overridden.</p>
 */
public abstract class AbstractRfSettings implements RfSettings, Cloneable {

	private RfTrackingPolicy trackingPolicy = RF_TRACKING_POLICY.getDefault();

	public AbstractRfSettings() {
	}

	@Override
	public final RfTrackingPolicy getTrackingPolicy() {
		return this.trackingPolicy;
	}

	@Override
	public final void setTrackingPolicy(RfTrackingPolicy trackingPolicy) {
		this.trackingPolicy =
				trackingPolicy != null
				? trackingPolicy : RF_TRACKING_POLICY.getDefault();
	}

	@Override
	public final void read(Parameters params) {
		setTrackingPolicy(params.valueOf(
				RF_TRACKING_POLICY,
				getTrackingPolicy()));
		readSettings(params);
	}

	@Override
	public final void write(Parameters params) {
		params.set(RF_TRACKING_POLICY, getTrackingPolicy());
		writeSettings(params);
	}

	@Override
	public AbstractRfSettings clone() {
		try {
			return (AbstractRfSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Reads this settings except tracking policy.
	 *
	 * @param params parameters to read the setting from.
	 */
	protected abstract void readSettings(Parameters params);

	/**
	 * Writes this settings except tracking policy.
	 *
	 * @param params parameters to write the settings to.
	 */
	protected abstract void writeSettings(Parameters params);

}
