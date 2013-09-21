package ru.aplix.ltk.driver.ctg.impl;

import static ru.aplix.ltk.driver.ctg.CtgRfSettings.CTG_RF_DEFAULT_READER_PORT;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.core.reader.RfReaderDriver;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;


final class CtgRfReaderDriver implements RfReaderDriver {

	private final CtgRfConnection connection;
	private final RfReader reader;
	private RfReaderContext context;
	private volatile CtgReaderThread thread;

	CtgRfReaderDriver(CtgRfConnection connection) {
		this.connection = connection;
		this.reader = new RfReader(this);
	}

	public final CtgRfConnection getConnection() {
		return this.connection;
	}

	public final RfReader getReader() {
		return this.reader;
	}

	public final CtgRfSettings getSettings() {
		return getConnection().getSettings();
	}

	@Override
	public final String getRfPortId() {

		final int readerPort = getSettings().getReaderPort();
		final String readerHost = getSettings().getReaderHost();

		if (readerPort == CTG_RF_DEFAULT_READER_PORT) {
			return readerHost;
		}

		return readerHost + ':' + readerPort;
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
		getConnection().getProvider().removeDriver(this);
		if (this.thread != null) {
			this.thread.stopReader();
			this.thread = null;
		}
	}


}
