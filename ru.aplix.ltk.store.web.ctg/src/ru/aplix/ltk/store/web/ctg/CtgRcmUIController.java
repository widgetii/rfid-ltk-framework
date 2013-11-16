package ru.aplix.ltk.store.web.ctg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;
import ru.aplix.ltk.store.web.rcm.ui.RcmUI;
import ru.aplix.ltk.store.web.rcm.ui.RcmUIController;
import ru.aplix.ltk.store.web.rcm.ui.RcmUIDesc;

@RcmUI
public class CtgRcmUIController
		extends RcmUIController<CtgRfSettings, CtgRcmUISettings> {

	private static final String[] SCRIPT_URLS =
			new String[] {"rcm/ui/ctg.js"};
	private static final String[] ANGULAR_MODULE_IDS =
			new String[] {"rfid-tag-store.rcm.ui.ctg"};
	private static final RcmUIDesc DESC =
			new RcmUIDesc()
			.mapping("rcm/ui/ctg.json")
			.settingsTemplateURL("rcm/ui/ctg.html");

	@Autowired
	@Qualifier("ctgRfProvider")
	private RfProvider<CtgRfSettings> provider;

	@Override
	public RfProvider<CtgRfSettings> getRfProvider() {
		return this.provider;
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
	public RcmUIDesc getDesc() {
		return DESC;
	}

	@Override
	public CtgRcmUISettings uiSettings(HttpRfProfile<CtgRfSettings> profile) {
		return new CtgRcmUISettings(profile);
	}

}
