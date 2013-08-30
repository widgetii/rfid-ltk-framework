package ru.aplix.ltk.driver.dummy;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfConnector;
import ru.aplix.ltk.driver.dummy.impl.DummyRfConnection;


public class DummyRfConnector implements RfConnector {

	private String portId = "TEST Port";
	private String deviceId = "TEST Reader";
	private long generationPeriod = 10000L;
	private long readPeriod = 1000L;

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

	public long getReadPeriod() {
		return this.readPeriod;
	}

	public void setReadPeriod(long readPeriod) {
		this.readPeriod = readPeriod;
	}

	@Override
	public RfConnection connect() {
		return new DummyRfConnection(this);
	}

}
