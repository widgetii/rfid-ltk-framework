package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfSettings;


public abstract class RcmUISettings<S extends RfSettings> {

	private String providerId;
	private String profileId;
	private String profileName;
	private boolean autostart;

	public RcmUISettings() {
	}

	public RcmUISettings(HttpRfProfile<S> profile) {
		this.providerId = profile.getProfileId().getProviderId();
		this.profileId = profile.getProfileId().getId();

		final ClrProfileSettings<S> settings = profile.getSettings();

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

	public final ClrProfileSettings<S> fill(
			ClrProfileSettings<S> settings) {
		settings.setProfileName(getProfileName());
		settings.setAutostart(isAutostart());
		fillSettings(settings.getSettings());
		return settings;
	}

	protected abstract void fillSettings(S settings);

}
