package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


@RcmUI
public class DefaultRcmUIController
		extends RcmUIController<RfSettings, RcmUISettings> {

	@Override
	public RfProvider<?> getRfProvider() {
		return null;
	}

	@Override
	public String getSettingsTemplateURL() {
		return "rcm/ui/default.html";
	}

	@Override
	public String getMapping() {
		return "rcm/ui/default.json";
	}

}
