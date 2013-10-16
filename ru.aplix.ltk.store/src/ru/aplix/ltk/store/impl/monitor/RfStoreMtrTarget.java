package ru.aplix.ltk.store.impl.monitor;

import ru.aplix.ltk.monitor.MonitoringTarget;


public class RfStoreMtrTarget extends MonitoringTarget<RfStoreMtr> {

	public static final RfStoreMtrTarget RF_STORE_MTR_TARGET =
			new RfStoreMtrTarget();

	private RfStoreMtrTarget() {
	}

	@Override
	public String toString() {
		return "RFID Store";
	}

	@Override
	protected RfStoreMtr newMonitoring() {
		return new RfStoreMtr();
	}

}
