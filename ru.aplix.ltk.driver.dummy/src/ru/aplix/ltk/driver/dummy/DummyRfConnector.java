package ru.aplix.ltk.driver.dummy;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfConnector;
import ru.aplix.ltk.driver.dummy.impl.DummyRfConnection;


public class DummyRfConnector implements RfConnector {

	private String portId = "TEST Port";
	private String deviceId = "TEST Reader";
	private long generationPeriod = 5000L;
	private long presenceDuration = 3000L;
	private long readPeriod = 1000L;
	private int maxTags = 5;

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
	public RfConnection connect() {
		return new DummyRfConnection(this);
	}

}
