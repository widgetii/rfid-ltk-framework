package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;


public abstract class RcmUISettings {

	private String providerId;
	private String profileId;
	private String profileName;
	private boolean autostart;

	public RcmUISettings() {
	}

	public RcmUISettings(HttpRfProfile<?> profile) {
		this.providerId = profile.getProfileId().getProviderId();
		this.profileId = profile.getProfileId().getId();

		final ClrProfileSettings<?> settings = profile.getSettings();

		this.profileName = settings.getProfileName();
		this.autostart = settings.isAutostart();
	}

	public String getProviderId() {
		return this.providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getProfileId() {
		return this.profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileName() {
		return this.profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public boolean isAutostart() {
		return this.autostart;
	}

	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

}
