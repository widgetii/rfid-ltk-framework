package ru.aplix.ltk.ui.impl;

import javax.swing.JComponent;

import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.ui.RfSettingsUI;
import ru.aplix.ltk.ui.RfSettingsUIContext;


final class DefaultRfConnectorUI implements RfSettingsUI<RfSettings> {

	private final RfSettingsUIContext<RfSettings> context;

	DefaultRfConnectorUI(RfSettingsUIContext<RfSettings> context) {
		this.context = context;
	}

	@Override
	public JComponent getUIComponent() {
		return null;
	}

	@Override
	public RfSettings buildSettings() {
		return this.context.getRfProvider().newSettings();
	}

}
