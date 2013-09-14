package ru.aplix.ltk.collector.http.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import ru.aplix.ltk.core.AbstractRfSettings;
import ru.aplix.ltk.core.util.Parameters;


public class HttpRfSettings extends AbstractRfSettings {

	private URL collectorURL;
	private URL clientURL;

	public URL getCollectorURL() {
		return this.collectorURL;
	}

	public void setCollectorURL(URL collectorURL) {
		ensureHttpURL(collectorURL);
		this.collectorURL = collectorURL;
	}

	public URL getClientURL() {
		return this.clientURL;
	}

	public void setClientURL(URL clientURL) {
		ensureHttpURL(clientURL);
		this.clientURL = clientURL;
	}

	@Override
	public HttpRfSettings clone() {
		return (HttpRfSettings) super.clone();
	}

	@Override
	protected void readSettings(Parameters params) {
		setCollectorURL(
				urlFromParams("collectorURL", params, getCollectorURL()));
		setClientURL(urlFromParams("clientURL", params, getClientURL()));
	}

	@Override
	protected void writeSettings(Parameters params) {
		params.set("collectorURL", getCollectorURL())
		.set("clientURL", getClientURL());
	}

	private void ensureHttpURL(URL url) {
		if (url != null) {

			final String protocol = url.getProtocol();

			if (!"http".equals(protocol) && !"https".equals(protocol)) {
				throw new IllegalArgumentException(
						"HTTP/HTTPS URL expected: " + url);
			}
		}
	}

	private URL urlFromParams(
			String name,
			Parameters params,
			URL noValue) {
		try {

			final String url = params.valueOf(
					name,
					null,
					Objects.toString(noValue, null));

			return url != null ? new URL(url) : null;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid client URL", e);
		}
	}

}
