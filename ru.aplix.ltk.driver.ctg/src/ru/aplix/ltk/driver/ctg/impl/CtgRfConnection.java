package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;


public class CtgRfConnection extends RfConnection {

	private final CtgRfSettings settings;

	public CtgRfConnection(CtgRfSettings settings) {
		super(settings);
		this.settings = settings;
	}

	public final CtgRfSettings getSettings() {
		return this.settings;
	}

	@Override
	protected RfSource createSource() {
		return new RfReader(new CtgRfReaderDriver(getSettings())).toRfSource();
	}

}
