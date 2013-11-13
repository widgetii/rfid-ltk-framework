package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;


public class DefaultRcmUISettings extends RcmUISettings {

	public DefaultRcmUISettings() {
	}

	public DefaultRcmUISettings(HttpRfProfile<?> profile) {
		super(profile);
	}

}
