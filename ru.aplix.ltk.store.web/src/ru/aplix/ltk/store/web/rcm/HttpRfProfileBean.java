package ru.aplix.ltk.store.web.rcm;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.store.web.rcm.ui.DefaultRcmUISettings;
import ru.aplix.ltk.store.web.rcm.ui.RcmUISettings;
import ru.aplix.ltk.store.web.receiver.RfReceiverDesc;


public class HttpRfProfileBean {

	private String id;
	private RfProviderDesc provider;
	private RfReceiverDesc receiver;
	private RcmUISettings<?> settings = new DefaultRcmUISettings();

	public HttpRfProfileBean() {
	}

	public HttpRfProfileBean(HttpRfProfile<?> profile) {
		setProfile(profile);
	}

	public void setProfile(HttpRfProfile<?> profile) {
		this.id = profile.getProfileId().urlEncode();
		this.provider = new RfProviderDesc(profile.getProvider());
	}

	public String getId() {
		return this.id;
	}

	public RfProviderDesc getProvider() {
		return this.provider;
	}

	public RfReceiverDesc getReceiver() {
		return this.receiver;
	}

	public void setReceiver(RfReceiverDesc receiver) {
		this.receiver = receiver;
	}

	public RcmUISettings<?> getSettings() {
		return this.settings;
	}

	public void setSettings(RcmUISettings<?> settings) {
		this.settings =
				settings != null ? settings : new DefaultRcmUISettings();
	}

}
