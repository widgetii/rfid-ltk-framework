package ru.aplix.ltk.ui.impl;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.ui.RfConnectorUI;
import ru.aplix.ltk.ui.RfConnectorUIContext;
import ru.aplix.ltk.ui.RfProviderUI;


public final class DefaultRfProviderUI implements RfProviderUI<RfProvider> {

	@Override
	public Class<? extends RfProvider> getProviderInterface() {
		return RfProvider.class;
	}

	@Override
	public RfConnectorUI newConnectorUI(
			RfConnectorUIContext<RfProvider> context) {
		return new DefaultRfConnectorUI(context);
	}

}
