package ru.aplix.ltk.driver.dummy;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfConnector;
import ru.aplix.ltk.driver.dummy.impl.DummyRfConnection;


public class DummyRfConnector implements RfConnector {

	private String portId = "TEST Port";
	private String deviceId = "TEST Reader";

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

	@Override
	public RfConnection connect() {
		return new DummyRfConnection(this);
	}

}
