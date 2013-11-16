package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfSettings;


public class DefaultRcmUISettings<S extends RfSettings>
		extends RcmUISettings<S> {

	public DefaultRcmUISettings() {
	}

	public DefaultRcmUISettings(HttpRfProfile<S> profile) {
		super(profile);
	}

	@Override
	protected void fillSettings(S settings) {
	}

}
