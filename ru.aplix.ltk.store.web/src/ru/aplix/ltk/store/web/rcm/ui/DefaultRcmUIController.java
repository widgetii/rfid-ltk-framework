package ru.aplix.ltk.store.web.rcm.ui;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfStore;


@RcmUI
public class DefaultRcmUIController<S extends RfSettings>
		extends RcmUIController<S, DefaultRcmUISettings<S>> {

	private static final String[] SCRIPT_URLS =
			new String[] {"rcm/ui/default.js"};
	private static final String[] ANGULAR_MODULE_IDS =
			new String[] {"rfid-tag-store.rcm.ui.default"};
	private static final RcmUIDesc DESC =
			new RcmUIDesc()
			.mapping("rcm/ui/default.json")
			.settingsTemplateURL("rcm/ui/default.html");

	@Autowired
	private RfStore rfStore;

	@Override
	public RfProvider<S> getRfProvider() {
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
	public RcmUIDesc getDesc() {
		return DESC;
	}

	@RequestMapping(
			value = "/rcm/ui/default.json",
			method = RequestMethod.PUT)
	@ResponseBody
	public RcmUIResponseBean save(
			@RequestParam("server") String serverURL,
			@RequestBody DefaultRcmUISettings<S> settings,
			HttpServletResponse resp) {
		return getContext().save(serverURL, settings, resp);
	}

	@Override
	public DefaultRcmUISettings<S> uiSettings(HttpRfProfile<S> profile) {
		return new DefaultRcmUISettings<>(profile);
	}

	@Override
	public ClrProfileSettings<S> profileSettings(
			DefaultRcmUISettings<S> uiSettings) {

		@SuppressWarnings("unchecked")
		final RfProvider<S> provider =
				(RfProvider<S>) this.rfStore.allRfProviders().get(
						uiSettings.getProviderId());

		if (provider == null) {
			throw new IllegalArgumentException(
					"Unknown provider: " + uiSettings.getProviderId());
		}

		return uiSettings.fill(new ClrProfileSettings<>(provider));
	}

}
