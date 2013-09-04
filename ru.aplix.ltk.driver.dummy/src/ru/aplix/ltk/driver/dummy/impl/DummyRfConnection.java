package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.core.reader.RfReaderDriver;
import ru.aplix.ltk.core.source.RfConnected;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.driver.dummy.DummyRfSettings;


public class DummyRfConnection extends RfConnection implements RfReaderDriver {

	private final DummyRfSettings settings;
	private final DummyTagsGenerator generator;
	private RfReaderContext context;

	public DummyRfConnection(DummyRfSettings settings) {
		super(settings);
		this.settings = settings;
		this.generator = new DummyTagsGenerator(this);
	}

	public final DummyRfSettings getSettings() {
		return this.settings;
	}

	public final RfReaderContext getContext() {
		return this.context;
	}

	@Override
	public final String getRfPortId() {
		return getSettings().getPortId();
	}

	@Override
	public void initRfReader(RfReaderContext context) {
		this.context = context;
	}

	@Override
	public void startRfReader() {
		this.context.updateStatus(
				new RfConnected(getSettings().getDeviceId()));
		this.generator.start();
	}

	@Override
	public void stopRfReader() {
		this.generator.stop();
	}

	@Override
	protected RfSource createSource() {
		return new RfReader(this).toRfSource();
	}

}
