package ru.aplix.ltk.store.web.rcm;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.store.web.receiver.RfReceiverDesc;


public class HttpRfProfileBean {

	private final String id;
	private final String providerId;
	private final String profileId;
	private final String name;
	private RfProviderDesc provider;
	private RfReceiverDesc receiver;

	public HttpRfProfileBean(HttpRfProfile<?> profile) {

		final ClrProfileId profileId = profile.getProfileId();
		
		this.id = profileId.urlEncode();
		this.providerId = profileId.getProviderId();
		this.profileId = profileId.getId();

		final ClrProfileSettings<?> settings = profile.getSettings();

		this.name = settings.getProfileName();
	}

	public String getId() {
		return this.id;
	}

	public String getProviderId() {
		return this.providerId;
	}

	public String getProfileId() {
		return this.profileId;
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
