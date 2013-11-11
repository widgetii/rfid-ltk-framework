package ru.aplix.ltk.store.web.rcm;

import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.store.web.receiver.RfReceiverDesc;


public class HttpRfProfileBean {

	private final String id;
	private final String name;
	private RfProviderDesc provider;
	private RfReceiverDesc receiver;

	public HttpRfProfileBean(HttpRfProfile<?> profile) {
		this.id = profile.getProfileId().urlEncode();

		final ClrProfileSettings<?> settings = profile.getSettings();

		this.name = settings.getProfileName();
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
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

}
