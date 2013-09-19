package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;
import ru.aplix.ltk.osgi.Logger;


public class CtgRfProvider implements RfProvider<CtgRfSettings> {

	private final Logger logger;

	public CtgRfProvider(Logger logger) {
		this.logger = logger;
	}

	@Override
	public String getId() {
		return "ctg";
	}

	@Override
	public String getName() {
		return "Contiguous RFID Reader";
	}

	@Override
	public Class<? extends CtgRfSettings> getSettingsType() {
		return CtgRfSettings.class;
	}

	public final Logger getLogger() {
		return this.logger;
	}

	@Override
	public CtgRfSettings newSettings() {
		return new CtgRfSettings();
	}

	@Override
	public CtgRfSettings copySettings(CtgRfSettings settings) {
		return settings.clone();
	}

	@Override
	public RfConnection connect(CtgRfSettings settings) {
		return new CtgRfConnection(this, settings.clone());
	}

}
