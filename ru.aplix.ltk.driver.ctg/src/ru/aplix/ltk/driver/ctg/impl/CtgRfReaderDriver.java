package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.core.reader.RfReaderDriver;
import ru.aplix.ltk.driver.ctg.CtgRfConnector;


final class CtgRfReaderDriver implements RfReaderDriver {

	private final CtgRfConfig config;
	private RfReaderContext context;
	private volatile CtgReaderThread thread;

	CtgRfReaderDriver(CtgRfConfig config) {
		this.config = config;
	}

	@Override
	public final String getRfPortId() {

		final int readerPort = this.config.getReaderPort();
		final String readerHost = this.config.getReaderHost();

		if (readerPort == CtgRfConnector.CTG_RF_DEFAULT_READER_PORT) {
			return readerHost;
		}

		return readerHost + ':' + readerPort;
	}

	public final CtgRfConfig getConfig() {
		return this.config;
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
		this.thread = new CtgReaderThread(this);
		this.thread.start();
	}

	@Override
	public void stopRfReader() {
		if (this.thread != null) {
			this.thread.stopReader();
			this.thread = null;
		}
	}


}
