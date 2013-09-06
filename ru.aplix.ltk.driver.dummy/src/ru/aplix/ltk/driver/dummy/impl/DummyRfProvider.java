package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.driver.dummy.DummyRfSettings;


public class DummyRfProvider implements RfProvider<DummyRfSettings> {

	@Override
	public String getId() {
		return "dummy";
	}

	@Override
	public String getName() {
		return "Dummy RFID";
	}

	@Override
	public Class<? extends DummyRfSettings> getSettingsType() {
		return DummyRfSettings.class;
	}

	@Override
	public DummyRfSettings newSettings() {
		return new DummyRfSettings();
	}

	@Override
	public RfConnection connect(DummyRfSettings settings) {
		return new DummyRfConnection(settings.clone());
	}

}
