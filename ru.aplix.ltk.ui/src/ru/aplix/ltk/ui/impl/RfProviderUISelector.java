package ru.aplix.ltk.ui.impl;

import static ru.aplix.ltk.ui.RfProviderUI.DEFAULT_RF_PROVIDER_UI;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.RfProviderHandlerSelector;
import ru.aplix.ltk.ui.RfProviderUI;


public final class RfProviderUISelector
		extends RfProviderHandlerSelector<RfProviderUI<?>> {

	@Override
	public RfProviderUI<?> getDefaultHandler() {
		return DEFAULT_RF_PROVIDER_UI;
	}

	@Override
	public Class<? extends RfSettings> supportedSettingsClass(
			RfProviderUI<?> handler) {
		return handler.getSettingsType();
	}

}
