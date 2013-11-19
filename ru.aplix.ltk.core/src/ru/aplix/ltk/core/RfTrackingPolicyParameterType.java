package ru.aplix.ltk.core;

import static ru.aplix.ltk.core.collector.RfTrackingPolicy.DEFAULT_TRACKING_POLICY;
import ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.util.ParameterType;
import ru.aplix.ltk.core.util.Parameters;


final class RfTrackingPolicyParameterType
		extends ParameterType<RfTrackingPolicy> {

	static final
	RfTrackingPolicyParameterType RF_TRACKING_POLICY_PARAMETER_TYPE =
			new RfTrackingPolicyParameterType();

	private RfTrackingPolicyParameterType() {
	}

	@Override
	public RfTrackingPolicy[] getValues(Parameters params, String name) {

		final Parameters tpParams = params.sub("trackingPolicy");
		final String tpClassName = tpParams.valueOf("", "", null);

		if (tpClassName == null) {
			return null;
		}
		if (tpClassName.isEmpty()) {
			return new RfTrackingPolicy[0];
		}
		if ("default".equals(tpClassName)) {
			// Default tracking policy has no parameters.
			return new RfTrackingPolicy[] {DEFAULT_TRACKING_POLICY};
		}

		final RfTrackingPolicy policy;

		if ("custom".equals(tpClassName)) {
			policy = new DefaultRfTrackingPolicy();
		} else {
			policy = createTrackingPolicy(tpClassName);
		}

		policy.read(tpParams);

		return new RfTrackingPolicy[] {policy};
	}

	@Override
	public void setValues(
			Parameters params,
			String name,
			RfTrackingPolicy[] values) {

		final Parameters tpParams = params.sub("trackingPolicy");
		final RfTrackingPolicy tp = values[0];

		if (tp == null) {
			return;
		}
		if (tp == DEFAULT_TRACKING_POLICY) {
			tpParams.set("", "default");
			return;
		}
		if (tp.getClass() == DefaultRfTrackingPolicy.class) {
			tpParams.set("", "custom");
		} else {
			tpParams.set("", tp.getClass().getName());
		}
		tp.write(tpParams);
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
