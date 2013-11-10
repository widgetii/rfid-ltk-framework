package ru.aplix.ltk.store.web.rcm;

import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;


public class HttpRfProfileBean {

	private final String id;
	private final String name;

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

}
