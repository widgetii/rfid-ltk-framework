package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.driver.ctg.CtgRfConnector;
import ru.aplix.ltk.driver.ctg.CtgRfProvider;


public class CtgRfProviderImpl implements CtgRfProvider {

	@Override
	public String getName() {
		return "Contiguous RFID Reader";
	}

	@Override
	public CtgRfConnector newConnector() {
		return new CtgRfConnectorImpl();
	}

}
