package ru.aplix.ltk.collector.http.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import ru.aplix.ltk.core.AbstractRfSettings;
import ru.aplix.ltk.core.util.Parameters;


public class HttpRfSettings extends AbstractRfSettings {

	public static final long HTTP_RF_DEFAULT_RECONNECTION_DELAY = 45000L;

	private URL collectorURL;
	private URL clientURL;
	private long reconnectionDelay = HTTP_RF_DEFAULT_RECONNECTION_DELAY;

	public URL getCollectorURL() {
		return this.collectorURL;
	}

	public final void setCollectorURL(URL collectorURL) {
		ensureHttpURL(collectorURL);
		this.collectorURL = collectorURL;
	}

	public final URL getClientURL() {
		return this.clientURL;
	}

	public final void setClientURL(URL clientURL) {
		ensureHttpURL(clientURL);
		this.clientURL = clientURL;
	}

	public final long getReconnectionDelay() {
		return this.reconnectionDelay;
	}

	public final void setReconnectionDelay(long delay) {
		this.reconnectionDelay =
				delay > 0 ? delay : HTTP_RF_DEFAULT_RECONNECTION_DELAY;
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
		setReconnectionDelay(params.longValueOf(
				"reconnectionDelay",
				HTTP_RF_DEFAULT_RECONNECTION_DELAY,
				getReconnectionDelay()));
	}

	@Override
	protected void writeSettings(Parameters params) {
		params.set("collectorURL", getCollectorURL())
		.set("clientURL", getClientURL())
		.set("reconnectionDelay", getReconnectionDelay());
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
