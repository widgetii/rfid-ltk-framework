package ru.aplix.ltk.store.web.rcm.ui;


public final class RcmUIDesc implements Cloneable {

	private String mapping;
	private String settingsTemplateURL;

	public final String getMapping() {
		return this.mapping;
	}

	public final RcmUIDesc mapping(String mapping) {
		this.mapping = mapping;
		return this;
	}

	public final String getSettingsTemplateURL() {
		return this.settingsTemplateURL;
	}

	public final RcmUIDesc settingsTemplateURL(String settingsTemplateURL) {
		this.settingsTemplateURL = settingsTemplateURL;
		return this;
	}

}