package ru.aplix.ltk.store.web.rcm;

import static ru.aplix.ltk.store.web.rcm.RcmController.handleError;

import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.ClrAddress;
import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.collector.http.client.HttpRfServer;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.web.rcm.ui.*;


final class RcmUIContextImpl
		<S extends RfSettings,
		U extends RcmUISettings<S>>
				implements RcmUIContext<S, U> {

	private final RcmController controller;
	private final RcmUIController<S, U> uiController;

	RcmUIContextImpl(
			RcmController controller,
			RcmUIController<S, U> uiController) {
		this.controller = controller;
		this.uiController = uiController;
	}

	@Override
	public RcmUIResponseBean save(
			String serverURL,
			U settings,
			HttpServletResponse resp) {

		final HttpRfServer server =
				this.controller.httpRfManager()
				.httpRfServer(new ClrAddress(serverURL));
		final ClrProfileSettings<S> profileSettings =
				this.uiController.profileSettings(settings);
		final HttpRfProfile<S> profile = server.profile(
				profileSettings.getProvider(),
				settings.getProfileId());

		final RcmUIResponseBean response = new RcmUIResponseBean(profile);

		response.setSettings(settings);

		try {
			profile.updateSettings(profileSettings);
		}  catch (Exception e) {
			handleError(e, response, resp);
		}

		return response;
	}

}