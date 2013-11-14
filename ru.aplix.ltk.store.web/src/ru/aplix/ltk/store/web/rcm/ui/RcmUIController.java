package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


public abstract class RcmUIController<
		S extends RfSettings,
		U extends RcmUISettings> {

	private RcmUIContext<S, U> context;

	public final RcmUIContext<S, U> getContext() {
		return this.context;
	}

	public abstract RfProvider<?> getRfProvider();

	public abstract String[] getScriptURLs();

	public abstract String[] getAngularModuleIds();

	public abstract String getSettingsTemplateURL();

	public abstract String getMapping();

	public abstract U uiSettings(HttpRfProfile<S> profile);

	public abstract ClrProfileSettings<S> profileSettings(U uiSettings);

	public void init(RcmUIContext<S, U> context) {
		this.context = context;
	}

}
