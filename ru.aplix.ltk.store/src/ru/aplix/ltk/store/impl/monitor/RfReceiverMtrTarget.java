package ru.aplix.ltk.store.impl.monitor;

import ru.aplix.ltk.monitor.MonitoringTarget;
import ru.aplix.ltk.store.RfReceiver;


public class RfReceiverMtrTarget extends MonitoringTarget<RfReceiverMtr> {

	private final RfReceiver<?> receiver;
	private final int id;

	public RfReceiverMtrTarget(RfReceiver<?> receiver) {
		this.receiver = receiver;
		this.id = receiver.getId();
	}

	public final RfReceiver<?> getRfReceiver() {
		return this.receiver;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final RfReceiverMtrTarget other = (RfReceiverMtrTarget) obj;

		return this.id == other.id;
	}

	@Override
	public String toString() {
		if (this.receiver == null) {
			return super.toString();
		}
		if (this.id == 0) {
			return "New RFID Receiver";
		}
		return this.receiver.toString();
	}

	@Override
	protected RfReceiverMtr newMonitoring() {
		return new RfReceiverMtr();
	}

}
