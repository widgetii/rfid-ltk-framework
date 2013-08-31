package ru.aplix.ltk.driver.ctg.ui;

import ru.aplix.ltk.driver.ctg.CtgRfProvider;
import ru.aplix.ltk.ui.RfConnectorUI;
import ru.aplix.ltk.ui.RfConnectorUIContext;
import ru.aplix.ltk.ui.RfProviderUI;


public class CtgRfProviderUI implements RfProviderUI<CtgRfProvider> {

	@Override
	public Class<? extends CtgRfProvider> getProviderInterface() {
		return CtgRfProvider.class;
	}

	@Override
	public RfConnectorUI newConnectorUI(
			RfConnectorUIContext<CtgRfProvider> context) {
		return new CtgRfConnectorUI(context);
	}

}
