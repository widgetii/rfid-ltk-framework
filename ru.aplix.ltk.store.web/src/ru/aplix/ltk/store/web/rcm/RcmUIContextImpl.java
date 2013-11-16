package ru.aplix.ltk.store.web.rcm;

import static ru.aplix.ltk.store.web.rcm.RcmController.handleError;

import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.ClrAddress;
import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.collector.http.client.HttpRfServer;
import ru.aplix.ltk.core.RfProvider;
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

	@SuppressWarnings("unchecked")
	@Override
	public RfProvider<S> rfProviderById(String providerId) {

		final RfProvider<?> provider =
				this.controller.rfStore().allRfProviders().get(providerId);

		if (provider == null) {
			throw new IllegalArgumentException(
					"Unknown provider: " + providerId);
		}

		final RfProvider<S> controllerProvider =
				this.uiController.getRfProvider();

		if (controllerProvider == null) {
			return (RfProvider<S>) provider;
		}

		if (!controllerProvider.getSettingsType().isAssignableFrom(
				provider.getSettingsType())) {
			throw new IllegalArgumentException(
					"Provider '" + providerId + "' is not compatible with '"
					+ controllerProvider.getId() + "'");
		}

		return (RfProvider<S>) provider;
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