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
		extends RcmUIController<S, DefaultRcmUISettings> {

	private static final String[] SCRIPT_URLS =
			new String[] {"rcm/ui/default.js"};
	private static final String[] ANGULAR_MODULE_IDS =
			new String[] {"rfid-tag-store.rcm.ui.default"};

	@Autowired
	private RfStore rfStore;

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

	@RequestMapping(
			value = "/rcm/ui/default.json",
			method = RequestMethod.PUT)
	@ResponseBody
	public RcmUIResponseBean save(
			@RequestParam("server") String serverURL,
			@RequestBody DefaultRcmUISettings settings,
			HttpServletResponse resp) {
		return getContext().save(serverURL, settings, resp);
	}

	@Override
	public DefaultRcmUISettings uiSettings(HttpRfProfile<S> profile) {
		return new DefaultRcmUISettings(profile);
	}

	@Override
	public ClrProfileSettings<S> profileSettings(
			DefaultRcmUISettings uiSettings) {

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
