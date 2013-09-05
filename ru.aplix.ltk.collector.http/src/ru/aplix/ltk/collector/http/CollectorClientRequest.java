package ru.aplix.ltk.collector.http;


public class CollectorClientRequest implements HttpRequest {

	private String clientURL;

	public CollectorClientRequest(HttpParams params) {
		decode(params);
	}

	public String getClientURL() {
		return this.clientURL;
	}

	public void setClientURL(String clientURL) {
		this.clientURL = clientURL;
	}

	@Override
	public void decode(HttpParams params) {
		setClientURL(params.valueOf("clientURL"));
	}

	@Override
	public HttpParams encode() {
		return new HttpParams(1).set("clientURL", getClientURL());
	}

}
