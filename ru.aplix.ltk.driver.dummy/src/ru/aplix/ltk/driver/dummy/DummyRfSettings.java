package ru.aplix.ltk.driver.dummy;

import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.driver.dummy.impl.DummyTrackingPolicy;


public class DummyRfSettings implements RfSettings, Cloneable {

	private RfTrackingPolicy trackingPolicy = new DummyTrackingPolicy(this);
	private String portId = "TEST Port";
	private String deviceId = "TEST Reader";
	private long generationPeriod = 5000L;
	private long presenceDuration = 3000L;
	private long readPeriod = 1000L;
	private int maxTags = 5;

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

	public void setPresenceDuration(long presenceDuration) {
		this.presenceDuration = presenceDuration;
	}

	public long getReadPeriod() {
		return this.readPeriod;
	}

	public void setReadPeriod(long readPeriod) {
		this.readPeriod = readPeriod;
	}

	public int getMaxTags() {
		return this.maxTags;
	}

	public void setMaxTags(int maxTags) {
		this.maxTags = maxTags;
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
