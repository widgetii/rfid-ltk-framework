package ru.aplix.ltk.driver.dummy;

import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.util.Parameters;
import ru.aplix.ltk.driver.dummy.impl.DummyTrackingPolicy;


public class DummyRfSettings implements RfSettings, Cloneable {

	public static final long DUMMY_DEFAULT_GENERATION_PERIOD = 5000L;
	public static final long DUMMY_DEFAULT_PRESENCE_DURATION = 3000L;
	public static final long DUMMY_DEFAULT_READ_PERIOD = 1000L;
	public static final int DUMMY_DEFAULT_MAX_TAGS = 5;

	private RfTrackingPolicy trackingPolicy = new DummyTrackingPolicy(this);
	private String portId = "TEST Port";
	private String deviceId = "TEST Reader";
	private long generationPeriod = DUMMY_DEFAULT_GENERATION_PERIOD;
	private long presenceDuration = DUMMY_DEFAULT_PRESENCE_DURATION;
	private long readPeriod = DUMMY_DEFAULT_READ_PERIOD;
	private int maxTags = DUMMY_DEFAULT_MAX_TAGS;

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
				duration > 0 ? duration : DUMMY_DEFAULT_PRESENCE_DURATION;
	}

	public long getReadPeriod() {
		return this.readPeriod;
	}

	public void setReadPeriod(long period) {
		this.readPeriod = period > 0 ? period : DUMMY_DEFAULT_READ_PERIOD;
	}

	public int getMaxTags() {
		return this.maxTags;
	}

	public void setMaxTags(int maxTags) {
		this.maxTags = maxTags > 0 ? maxTags : DUMMY_DEFAULT_MAX_TAGS;
	}

	@Override
	public void read(Parameters params) {
		setDeviceId(params.valueOf("deviceId", getDeviceId()));
		setGenerationPeriod(params.longValueOf(
				"generationPeriod",
				DUMMY_DEFAULT_GENERATION_PERIOD,
				getGenerationPeriod()));
		setPresenceDuration(params.longValueOf(
				"presenceDuration",
				DUMMY_DEFAULT_PRESENCE_DURATION,
				getPresenceDuration()));
		setReadPeriod(params.longValueOf(
				"readPeriod",
				DUMMY_DEFAULT_READ_PERIOD,
				getReadPeriod()));
		setMaxTags(params.intValueOf(
				"maxTags",
				DUMMY_DEFAULT_MAX_TAGS,
				getMaxTags()));
	}

	@Override
	public void write(Parameters params) {
		params.set("deviceId", getDeviceId())
		.set("generationPeriod", getGenerationPeriod())
		.set("presenceDuration", getPresenceDuration())
		.set("readPeriod", getReadPeriod())
		.set("maxTags", getMaxTags());
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
