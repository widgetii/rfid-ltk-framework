package ru.aplix.ltk.core;

import static ru.aplix.ltk.core.collector.RfTrackingPolicy.DEFAULT_TRACKING_POLICY;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.util.Parameters;


/**
 * Abstract RFID settings implementation.
 *
 * <p>Uses the {@link RfTrackingPolicy#DEFAULT_TRACKING_POLICY default tracking
 * policy} if not overridden.</p>
 */
public abstract class AbstractRfSettings implements RfSettings, Cloneable {

	private RfTrackingPolicy trackingPolicy;

	public AbstractRfSettings() {
		this.trackingPolicy = defaultTrackingPolicy();
	}

	public RfTrackingPolicy defaultTrackingPolicy() {
		return DEFAULT_TRACKING_POLICY;
	}

	@Override
	public final RfTrackingPolicy getTrackingPolicy() {
		return this.trackingPolicy;
	}

	@Override
	public final void setTrackingPolicy(RfTrackingPolicy trackingPolicy) {
		this.trackingPolicy =
				trackingPolicy != null
				? trackingPolicy : defaultTrackingPolicy();
	}

	@Override
	public final void read(Parameters params) {
		readTrackingPolicy(params);
		readSettings(params);
	}

	@Override
	public final void write(Parameters params) {
		writeTrackingPolicy(params);
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

	protected abstract void readSettings(Parameters params);

	protected abstract void writeSettings(Parameters params);

	protected void readTrackingPolicy(Parameters params) {

		final Parameters tpParams = params.sub("trackingPolicy");
		final String tpClassName = tpParams.valueOf("", "", null);

		if (tpClassName != null) {
			if (tpClassName.isEmpty()) {
				setTrackingPolicy(defaultTrackingPolicy());
			} else if ("default".equals(tpClassName)) {
				setTrackingPolicy(DEFAULT_TRACKING_POLICY);
				return;// Default tracking policy has no parameters.
			} else {
				setTrackingPolicy(createTrackingPolicy(tpClassName));
			}
		}

		getTrackingPolicy().read(tpParams);
	}

	protected void writeTrackingPolicy(Parameters params) {

		final Parameters tcParams = params.sub("trackingPolicy");
		final RfTrackingPolicy tp = getTrackingPolicy();

		if (tp == DEFAULT_TRACKING_POLICY) {
			tcParams.set("", "default");
			return;
		}

		tcParams.set("class", tp.getClass().getName());
		tp.write(tcParams);
	}

	private RfTrackingPolicy createTrackingPolicy(String className) {
		try {

			@SuppressWarnings("unchecked")
			final Class<RfTrackingPolicy> tpClass =
					(Class<RfTrackingPolicy>) Class.forName(className);

			return tpClass.newInstance();
		} catch (ClassNotFoundException
				| InstantiationException
				| IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
