package ru.aplix.ltk.driver.dummy;

import static ru.aplix.ltk.core.util.NumericParameterType.INTEGER_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.STRING_PARAMETER_TYPE;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.util.Parameter;
import ru.aplix.ltk.core.util.Parameters;
import ru.aplix.ltk.driver.dummy.impl.DummyTrackingPolicy;


public class DummyRfSettings implements RfSettings, Cloneable {

	public static final Parameter<String> DUMMY_DEVICE_ID =
			STRING_PARAMETER_TYPE.parameter("deviceId")
			.byDefault("TEST Reader");
	public static final Parameter<Long> DUMMY_GENERATION_PERIOD =
			LONG_PARAMETER_TYPE.parameter("generationPeriod")
			.byDefault(5000L);
	public static final Parameter<Long> DUMMY_PRESENCE_DURATION =
			LONG_PARAMETER_TYPE.parameter("presenceDuration")
			.byDefault(3000L);
	public static final Parameter<Long> DUMMY_READ_PERIOD =
			LONG_PARAMETER_TYPE.parameter("readPeriod")
			.byDefault(1000L);
	public static final Parameter<Integer> DUMMY_MAX_TAGS =
			INTEGER_PARAMETER_TYPE.parameter("maxTags").byDefault(5);

	private RfTrackingPolicy trackingPolicy = new DummyTrackingPolicy(this);
	private String portId = "TEST Port";
	private String deviceId = DUMMY_DEVICE_ID.getDefault();
	private long generationPeriod = DUMMY_GENERATION_PERIOD.getDefault();
	private long presenceDuration = DUMMY_PRESENCE_DURATION.getDefault();
	private long readPeriod = DUMMY_READ_PERIOD.getDefault();
	private int maxTags = DUMMY_MAX_TAGS.getDefault();

	@Override
	public String getTargetId() {
		return "dummy";
	}

	@Override
	public RfTrackingPolicy getTrackingPolicy() {
		return this.trackingPolicy;
	}

	@Override
	public void setTrackingPolicy(RfTrackingPolicy trackingPolicy) {
		this.trackingPolicy =
				trackingPolicy != null
				? trackingPolicy : new DummyTrackingPolicy(this);
	}

	public String getPortId() {
		return this.portId;
	}

	public void setPortId(String portId) {
		this.portId = portId;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public long getGenerationPeriod() {
		return this.generationPeriod;
	}

	public void setGenerationPeriod(long generationPeriod) {
		this.generationPeriod = generationPeriod;
	}

	public long getPresenceDuration() {
		return this.presenceDuration;
	}

	public void setPresenceDuration(long duration) {
		this.presenceDuration =
				duration > 0 ? duration : DUMMY_PRESENCE_DURATION.getDefault();
	}

	public long getReadPeriod() {
		return this.readPeriod;
	}

	public void setReadPeriod(long period) {
		this.readPeriod = period > 0 ? period : DUMMY_READ_PERIOD.getDefault();
	}

	public int getMaxTags() {
		return this.maxTags;
	}

	public void setMaxTags(int maxTags) {
		this.maxTags = maxTags > 0 ? maxTags : DUMMY_MAX_TAGS.getDefault();
	}

	@Override
	public void read(Parameters params) {
		setDeviceId(params.valueOf(
				DUMMY_DEVICE_ID,
				getDeviceId()));
		setGenerationPeriod(params.valueOf(
				DUMMY_GENERATION_PERIOD,
				getGenerationPeriod()));
		setPresenceDuration(params.valueOf(
				DUMMY_PRESENCE_DURATION,
				getPresenceDuration()));
		setReadPeriod(params.valueOf(
				DUMMY_READ_PERIOD,
				getReadPeriod()));
		setMaxTags(params.valueOf(
				DUMMY_MAX_TAGS,
				getMaxTags()));
	}

	@Override
	public void write(Parameters params) {
		params.set(DUMMY_DEVICE_ID, getDeviceId())
		.set(DUMMY_GENERATION_PERIOD, getGenerationPeriod())
		.set(DUMMY_PRESENCE_DURATION, getPresenceDuration())
		.set(DUMMY_READ_PERIOD, getReadPeriod())
		.set(DUMMY_MAX_TAGS, getMaxTags());
	}

	@Override
	public DummyRfSettings clone() {
		try {
			return (DummyRfSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new UnsupportedOperationException();
		}
	}

}
