package ru.aplix.ltk.store.web.rcm;

import ru.aplix.ltk.store.web.rcm.ui.RcmUIController;


public final class RcmUIDesc {

	private final String mapping;
	private final String settingsTemplateURL;

	RcmUIDesc(RcmUIController<?, ?> controller) {
		this.mapping = controller.getMapping();
		this.settingsTemplateURL = controller.getSettingsTemplateURL();
	}

	public String getMapping() {
		return this.mapping;
	}

	public String getSettingsTemplateURL() {
		return this.settingsTemplateURL;
	}

}