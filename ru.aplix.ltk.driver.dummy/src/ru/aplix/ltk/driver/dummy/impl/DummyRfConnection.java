package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.reader.RfReaderConnected;
import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.core.reader.RfReaderDriver;
import ru.aplix.ltk.driver.dummy.DummyRfConnector;


public class DummyRfConnection extends RfConnection implements RfReaderDriver {

	private final String rfPortId;
	private final String rfDeviceId;
	private final DummyTagsGenerator generator;
	private RfReaderContext context;

	public DummyRfConnection(DummyRfConnector connector) {
		this.rfPortId = connector.getPortId();
		this.rfDeviceId = connector.getDeviceId();
		this.generator = new DummyTagsGenerator(connector, this);
	}

	@Override
	public final String getRfPortId() {
		return this.rfPortId;
	}

	public final String getRfDeviceId() {
		return this.rfDeviceId;
	}

	public final RfReaderContext getContext() {
		return this.context;
	}

	@Override
	public void initRfReader(RfReaderContext context) {
		this.context = context;
	}

	@Override
	public void startRfReader() {
		this.context.updateStatus(new RfReaderConnected(this.rfDeviceId));
		this.generator.start();
	}

	@Override
	public void stopRfReader() {
		this.generator.stop();
	}

	@Override
	protected RfReaderDriver createReaderDriver() {
		return this;
	}

}