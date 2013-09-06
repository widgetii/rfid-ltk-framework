package ru.aplix.ltk.collector.http;

import ru.aplix.ltk.core.util.HttpObject;
import ru.aplix.ltk.core.util.HttpParams;


public class CollectorClientRequest implements HttpObject {

	private String clientURL;

	public CollectorClientRequest(HttpParams params) {
		httpDecode(params);
	}

	public String getClientURL() {
		return this.clientURL;
	}

	public void setClientURL(String clientURL) {
		this.clientURL = clientURL;
	}

	@Override
	public void httpDecode(HttpParams params) {
		setClientURL(params.valueOf("clientURL"));
	}

	@Override
	public void httpEncode(HttpParams params) {
		params.set("clientURL", getClientURL());
	}

}
