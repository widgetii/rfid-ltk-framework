package ru.aplix.ltk.core;

import static ru.aplix.ltk.core.collector.RfTrackingPolicy.DEFAULT_TRACKING_POLICY;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.util.HttpParams;


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
	public final void httpDecode(HttpParams params) {
		httpDecodeTrackingPolicy(params);
		httpDecodeSettings(params);
	}

	@Override
	public final void httpEncode(HttpParams params) {
		httpEncodeTrackingPolicy(params);
		httpEncodeSettings(params);
	}


	@Override
	public AbstractRfSettings clone() {
		try {
			return (AbstractRfSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

	protected abstract void httpDecodeSettings(HttpParams params);

	protected void httpEncodeTrackingPolicy(HttpParams params) {

		final HttpParams tcParams = params.sub("trackingPolicy");
		final RfTrackingPolicy tp = getTrackingPolicy();

		tcParams.set("class", tp.getClass().getName());
		tp.httpEncode(tcParams);
	}

	protected void httpDecodeTrackingPolicy(HttpParams params) {

		final HttpParams tpParams = params.sub("trackingPolicy");
		final String tpClassName = tpParams.valueOf("class", "", null);

		if (tpClassName == null) {
			return;
		}
		if (tpClassName.isEmpty()) {
			setTrackingPolicy(DEFAULT_TRACKING_POLICY);
			return;
		}

		final RfTrackingPolicy tp;

		try {

			@SuppressWarnings("unchecked")
			final Class<RfTrackingPolicy> tpClass =
					(Class<RfTrackingPolicy>) Class.forName(tpClassName);

			tp = tpClass.newInstance();
		} catch (ClassNotFoundException
				| InstantiationException
				| IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		setTrackingPolicy(tp);
		tp.httpDecode(tpParams);
	}

	protected abstract void httpEncodeSettings(HttpParams params);

}
