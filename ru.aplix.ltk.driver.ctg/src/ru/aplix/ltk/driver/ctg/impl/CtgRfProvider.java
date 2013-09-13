package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;


public class CtgRfProvider implements RfProvider<CtgRfSettings> {

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
		return new CtgRfConnection(settings.clone());
	}

}
