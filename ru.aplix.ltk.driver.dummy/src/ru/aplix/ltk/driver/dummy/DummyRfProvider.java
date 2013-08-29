package ru.aplix.ltk.driver.dummy;

import ru.aplix.ltk.core.RfProvider;


public class DummyRfProvider implements RfProvider {

	@Override
	public String getName() {
		return "Dummy RFID";
	}

	@Override
	public DummyRfConnector newConnector() {
		return new DummyRfConnector();
	}

}
