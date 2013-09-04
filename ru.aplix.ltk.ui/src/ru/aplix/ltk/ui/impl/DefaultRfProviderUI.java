package ru.aplix.ltk.ui.impl;

import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.ui.RfProviderUI;
import ru.aplix.ltk.ui.RfSettingsUI;
import ru.aplix.ltk.ui.RfSettingsUIContext;


public final class DefaultRfProviderUI implements RfProviderUI<RfSettings> {

	@Override
	public Class<? extends RfSettings> getSettingsType() {
		return RfSettings.class;
	}

	@Override
	public RfSettingsUI<RfSettings> newSettingsUI(
			RfSettingsUIContext<RfSettings> context) {
		return new DefaultRfConnectorUI(context);
	}

}
