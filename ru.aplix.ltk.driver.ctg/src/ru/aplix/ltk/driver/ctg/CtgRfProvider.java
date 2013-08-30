package ru.aplix.ltk.driver.ctg;

import ru.aplix.ltk.core.RfProvider;


public interface CtgRfProvider extends RfProvider {

	@Override
	CtgRfConnector newConnector();

}
