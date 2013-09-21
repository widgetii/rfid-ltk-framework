package ru.aplix.ltk.core;

import static ru.aplix.ltk.core.RfTrackingPolicyParameterType.RF_TRACKING_POLICY_PARAMETER_TYPE;
import static ru.aplix.ltk.core.collector.RfTrackingPolicy.DEFAULT_TRACKING_POLICY;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.util.Parameter;
import ru.aplix.ltk.core.util.Parameterized;


/**
 * RFID settings.
 *
 * <p>A settings instance can be constructed by {@link RfProvider#newSettings()
 * RFID provider}</p>
 *
 * <p>The settings are intended to be used to configure an RFID connection,
 * and then - for {@link RfProvider#connect(RfSettings) opening it}.</p>
 *
 * <p>Settings are not expected to be thread-safe.</p>
 *
 * <p>The changes to the settings instance made after RFID connection
 * {@link RfProvider#connect(RfSettings) opened} should not affect the this
 * connection, but can be reused to open another connection.</p>
 */
public interface RfSettings extends Parameterized {

	/**
	 * RFID tracking policy parameter.
	 */
	Parameter<RfTrackingPolicy> RF_TRACKING_POLICY =
			RF_TRACKING_POLICY_PARAMETER_TYPE.parameter("trackingPolicy")
			.byDefault(DEFAULT_TRACKING_POLICY);

	/**
	 * RFID tracking policy.
	 *
	 * @return tracking policy.
	 */
	RfTrackingPolicy getTrackingPolicy();

	/**
	 * Updates RFID tracking policy.
	 *
	 * @param trackingPolicy new tracking policy, or <code>null</code> to use a
	 * default one, specific to the {@link RfProvider provider}.
	 */
	void setTrackingPolicy(RfTrackingPolicy trackingPolicy);

}
