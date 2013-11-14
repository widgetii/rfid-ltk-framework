package ru.aplix.ltk.store.web.rcm;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.ClrAddress;
import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.*;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.web.rcm.ui.*;


final class RcmUIContextImpl
		<S extends RfSettings,
		U extends RcmUISettings>
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
		}  catch (InvalidHttpRfResponseException e) {
			response.setError("Не похоже на сервер накопителя");
		} catch (HttpRfServerException e) {
			response.setError(RcmController.errorMessage(e));
			resp.setStatus(e.getStatusCode());
		} catch (IOException e) {
			response.setError(e.toString());
			resp.setStatus(SC_INTERNAL_SERVER_ERROR);
		}

		return response;
	}

}