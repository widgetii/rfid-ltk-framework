package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.reader.RfReaderDriver;


public class CtgRfConnection extends RfConnection {

	private final CtgRfConfig config;

	public CtgRfConnection(CtgRfConfig config) {
		this.config = config;
	}

	public final CtgRfConfig getConfig() {
		return this.config;
	}

	@Override
	protected RfReaderDriver createReaderDriver() {
		return new CtgRfReaderDriver(this.config);
	}

}
