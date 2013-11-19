package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfSettings;


public class DefaultRcmUISettings extends RcmUISettings<RfSettings> {

	public DefaultRcmUISettings() {
	}

	public DefaultRcmUISettings(HttpRfProfile<RfSettings> profile) {
		super(profile);
	}

	@Override
	protected void fillSettings(RfSettings settings) {
	}

}
