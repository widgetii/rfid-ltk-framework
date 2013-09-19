package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;


public class CtgRfConnection extends RfConnection {

	private final CtgRfSettings settings;
	private final CtgRfProvider provider;

	public CtgRfConnection(CtgRfProvider provider, CtgRfSettings settings) {
		super(settings);
		this.provider = provider;
		this.settings = settings;
	}

	public final CtgRfSettings getSettings() {
		return this.settings;
	}

	public final CtgRfProvider getProvider() {
		return this.provider;
	}

	@Override
	protected RfSource createSource() {
		return new RfReader(new CtgRfReaderDriver(this)).toRfSource();
	}

}
