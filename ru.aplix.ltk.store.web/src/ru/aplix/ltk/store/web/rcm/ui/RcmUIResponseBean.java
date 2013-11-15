package ru.aplix.ltk.store.web.rcm.ui;

import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.store.web.ErrorBean;
import ru.aplix.ltk.store.web.rcm.HttpRfProfileBean;


public class RcmUIResponseBean extends HttpRfProfileBean implements ErrorBean {

	private String error;

	public RcmUIResponseBean() {
	}

	public RcmUIResponseBean(HttpRfProfile<?> profile) {
		super(profile);
	}

	@Override
	public String getError() {
		return this.error;
	}

	@Override
	public void setError(String error) {
		this.error = error;
	}

}
