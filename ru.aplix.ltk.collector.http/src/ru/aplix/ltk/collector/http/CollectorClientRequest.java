package ru.aplix.ltk.collector.http;

import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class CollectorClientRequest implements Parameterized {

	private String clientURL;

	public CollectorClientRequest(Parameters params) {
		read(params);
	}

	public String getClientURL() {
		return this.clientURL;
	}

	public void setClientURL(String clientURL) {
		this.clientURL = clientURL;
	}

	@Override
	public void read(Parameters params) {
		setClientURL(params.valueOf("clientURL"));
	}

	@Override
	public void write(Parameters params) {
		params.set("clientURL", getClientURL());
	}

}
