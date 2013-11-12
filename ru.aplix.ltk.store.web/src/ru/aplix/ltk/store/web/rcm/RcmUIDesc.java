package ru.aplix.ltk.store.web.rcm;

import ru.aplix.ltk.store.web.rcm.ui.RcmUIController;


public final class RcmUIDesc {

	private final String mapping;
	private final String settingsTemplateURL;

	RcmUIDesc(RcmUIController<?, ?> provider) {
		this.mapping = provider.getMapping();
		this.settingsTemplateURL = provider.getSettingsTemplateURL();
	}

	public String getMapping() {
		return this.mapping;
	}

	public String getSettingsTemplateURL() {
		return this.settingsTemplateURL;
	}

}