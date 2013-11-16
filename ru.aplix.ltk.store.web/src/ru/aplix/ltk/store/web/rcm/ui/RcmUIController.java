package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


public abstract class RcmUIController<
		S extends RfSettings,
		U extends RcmUISettings<S>> {

	private RcmUIContext<S, U> context;

	public final RcmUIContext<S, U> getContext() {
		return this.context;
	}

	public abstract RfProvider<S> getRfProvider();

	public abstract String[] getScriptURLs();

	public abstract String[] getAngularModuleIds();

	public abstract RcmUIDesc getDesc();

	public abstract U uiSettings(HttpRfProfile<S> profile);

	public ClrProfileSettings<S> profileSettings(U uiSettings) {

		final RfProvider<S> provider =
				getContext().rfProviderById(uiSettings.getProviderId());

		if (provider == null) {
			throw new IllegalArgumentException(
					"Unknown provider: " + uiSettings.getProviderId());
		}

		return uiSettings.fill(new ClrProfileSettings<>(provider));
	}

	public void init(RcmUIContext<S, U> context) {
		this.context = context;
	}

}
