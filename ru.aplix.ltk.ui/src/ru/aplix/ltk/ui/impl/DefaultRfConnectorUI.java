package ru.aplix.ltk.ui.impl;

import java.awt.Component;

import ru.aplix.ltk.core.RfConnector;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.ui.RfConnectorUI;
import ru.aplix.ltk.ui.RfConnectorUIContext;


final class DefaultRfConnectorUI implements RfConnectorUI {

	private final RfConnectorUIContext<RfProvider> context;

	DefaultRfConnectorUI(RfConnectorUIContext<RfProvider> context) {
		this.context = context;
	}

	@Override
	public Component getSettingsUI() {
		return null;
	}

	@Override
	public RfConnector buildConnector() {
		return this.context.getRfProvider().newConnector();
	}

}
