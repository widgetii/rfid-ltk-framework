package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.reader.RfReaderConnected;
import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.core.reader.RfReaderDriver;
import ru.aplix.ltk.driver.dummy.DummyRfConnector;


public class DummyRfConnection extends RfConnection implements RfReaderDriver {

	private final String rfPortId;
	private final String rfDeviceId;
	private RfReaderContext context;

	public DummyRfConnection(DummyRfConnector connector) {
		this.rfPortId = connector.getPortId();
		this.rfDeviceId = connector.getDeviceId();
	}

	@Override
	public final String getRfPortId() {
		return this.rfPortId;
	}

	public final String getRfDeviceId() {
		return this.rfDeviceId;
	}

	@Override
	public void initRfReader(RfReaderContext context) {
		this.context = context;
		this.context.updateStatus(new RfReaderConnected(this.rfDeviceId));
	}

	@Override
	public void startRfReader() {
	}

	@Override
	public void stopRfReader() {
	}

	@Override
	protected RfReaderDriver createReaderDriver() {
		return this;
	}

}
