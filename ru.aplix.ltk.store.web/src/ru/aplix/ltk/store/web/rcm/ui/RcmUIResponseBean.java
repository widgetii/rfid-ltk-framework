package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.store.web.rcm.HttpRfProfileBean;


public class RcmUIResponseBean extends HttpRfProfileBean {

	private String error;

	public RcmUIResponseBean(HttpRfProfile<?> profile) {
		super(profile);
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
