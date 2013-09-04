package ru.aplix.ltk.driver.ctg.ui;

import ru.aplix.ltk.driver.ctg.CtgRfSettings;
import ru.aplix.ltk.ui.RfProviderUI;
import ru.aplix.ltk.ui.RfSettingsUI;
import ru.aplix.ltk.ui.RfSettingsUIContext;


public class CtgRfProviderUI implements RfProviderUI<CtgRfSettings> {

	@Override
	public Class<? extends CtgRfSettings> getSettingsType() {
		return CtgRfSettings.class;
	}

	@Override
	public RfSettingsUI<CtgRfSettings> newSettingsUI(
			RfSettingsUIContext<CtgRfSettings> context) {
		return new CtgRfSettingsUI(context);
	}

}
