package ru.aplix.ltk.store.web.rcm;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.store.web.rcm.ui.RcmUISettings;
import ru.aplix.ltk.store.web.receiver.RfReceiverDesc;


public class HttpRfProfileBean {

	private final String id;
	private RfProviderDesc provider;
	private RfReceiverDesc receiver;
	private RcmUISettings settings;

	public HttpRfProfileBean(HttpRfProfile<?> profile) {
		this.id = profile.getProfileId().urlEncode();
	}

	public String getId() {
		return this.id;
	}

	public RfProviderDesc getProvider() {
		return this.provider;
	}

	public void setProvider(RfProviderDesc provider) {
		this.provider = provider;
	}

	public RfReceiverDesc getReceiver() {
		return this.receiver;
	}

	public void setReceiver(RfReceiverDesc receiver) {
		this.receiver = receiver;
	}

	public RcmUISettings getSettings() {
		return this.settings;
	}

	public void setSettings(RcmUISettings settings) {
		this.settings = settings;
	}

}
