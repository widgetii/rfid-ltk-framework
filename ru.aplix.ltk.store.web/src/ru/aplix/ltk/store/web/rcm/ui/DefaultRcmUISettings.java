package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;


public class DefaultRcmUISettings extends RcmUISettings {

	private String providerId;

	public DefaultRcmUISettings() {
	}

	public DefaultRcmUISettings(HttpRfProfile<?> profile) {
		super(profile);
		this.providerId = profile.getProfileId().getProviderId();
	}

	public String getProviderId() {
		return this.providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

}
