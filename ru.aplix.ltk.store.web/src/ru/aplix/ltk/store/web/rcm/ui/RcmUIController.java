package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


public abstract class RcmUIController<
		S extends RfSettings,
		U extends RcmUISettings> {

	private RcmUIContext context;

	public final RcmUIContext getContext() {
		return this.context;
	}

	public abstract RfProvider<?> getRfProvider();

	public abstract String getSettingsTemplateURL();

	public abstract String getMapping();

	public void init(RcmUIContext context) {
		this.context = context;
	}

}
