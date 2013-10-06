package ru.aplix.ltk.driver.blackbox.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.driver.blackbox.BlackboxRfSettings;


class BlackboxRfProvider implements RfProvider<BlackboxRfSettings> {

	@Override
	public String getId() {
		return "blackbox";
	}

	@Override
	public String getName() {
		return "Blackbox RFID Driver";
	}

	@Override
	public Class<? extends BlackboxRfSettings> getSettingsType() {
		return BlackboxRfSettings.class;
	}

	@Override
	public BlackboxRfSettings newSettings() {
		return new BlackboxRfSettings();
	}

	@Override
	public BlackboxRfSettings copySettings(BlackboxRfSettings settings) {
		return settings.clone();
	}

	@Override
	public RfConnection connect(BlackboxRfSettings settings) {
		return new BlackboxRfConnection(settings.clone());
	}

}
