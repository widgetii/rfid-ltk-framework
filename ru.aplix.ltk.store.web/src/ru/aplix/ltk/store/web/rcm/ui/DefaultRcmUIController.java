package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


@RcmUI
public class DefaultRcmUIController
		extends RcmUIController<RfSettings, DefaultRcmUISettings> {

	private static final String[] SCRIPT_URLS =
			new String[] {"rcm/ui/default.js"};
	private static final String[] ANGULAR_MODULE_IDS =
			new String[] {"rfid-tag-store.rcm.ui.default"};

	@Override
	public RfProvider<?> getRfProvider() {
		return null;
	}

	@Override
	public String[] getScriptURLs() {
		return SCRIPT_URLS;
	}

	@Override
	public String[] getAngularModuleIds() {
		return ANGULAR_MODULE_IDS;
	}

	@Override
	public String getSettingsTemplateURL() {
		return "rcm/ui/default.html";
	}

	@Override
	public String getMapping() {
		return "rcm/ui/default.json";
	}

	@Override
	public DefaultRcmUISettings uiSettings(HttpRfProfile<RfSettings> profile) {
		return new DefaultRcmUISettings(profile);
	}

}
