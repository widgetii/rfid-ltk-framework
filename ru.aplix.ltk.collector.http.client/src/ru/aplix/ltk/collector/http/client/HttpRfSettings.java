package ru.aplix.ltk.collector.http.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import ru.aplix.ltk.collector.http.ClrClientRequest;
import ru.aplix.ltk.core.AbstractRfSettings;
import ru.aplix.ltk.core.util.Parameters;


/**
 * RFID settings for HTTP collector's client.
 *
 * <p>An RFID provider supporting such settings is actually a client of the
 * remote HTTP collector, and receives the messages from it.</p>
 *
 * <p>The {@link #getCollectorURL() collector URL}, and {@link #getClientURL()
 * client URL} are mandatory. Other options have reasonable defaults.</p>
 */
public class HttpRfSettings extends AbstractRfSettings {

	/**
	 * Default HTTP collector reconnection delay.
	 *
	 * @see #getReconnectionDelay()
	 */
	public static final long HTTP_RF_DEFAULT_RECONNECTION_DELAY = 45000L;

	private URL collectorURL;
	private URL clientURL;
	private long reconnectionDelay = HTTP_RF_DEFAULT_RECONNECTION_DELAY;

	/**
	 * HTTP collector URL
	 *
	 * @return http or https URL of the collector.
	 */
	public URL getCollectorURL() {
		return this.collectorURL;
	}

	/**
	 * Sets collector URL.
	 *
	 * @param collectorURL new http or https URL of the collector.
	 */
	public final void setCollectorURL(URL collectorURL) {
		ensureHttpURL(collectorURL);
		this.collectorURL = collectorURL;
	}

	/**
	 * Client URL.
	 *
	 * <p>This is an URL the HTTP collector will send data to. It is passed
	 * to the collector with {@link ClrClientRequest client request}.</p></p>
	 *
	 * @return http or https client URL.
	 */
	public final URL getClientURL() {
		return this.clientURL;
	}

	/**
	 * Sets client URL.
	 *
	 * @param clientURL new http or https client URL.
	 */
	public final void setClientURL(URL clientURL) {
		ensureHttpURL(clientURL);
		this.clientURL = clientURL;
	}

	/**
	 * HTTP collector reconnection delay.
	 *
	 * <p>An HTTP collector periodically sends the ping requests to each of its
	 * clients, if it has no data to send. This indicates that the collector is
	 * online. So, if the client don't receive any requests from the collector
	 * for some time, it considers the connection is lost, and starts attempts
	 * to re-establish it.</p>
	 *
	 * <p>This option indicate how many time should pass since the last request
	 * to client to assume that connection in lost.</p>
	 *
	 * @return delay in milliseconds.
	 */
	public final long getReconnectionDelay() {
		return this.reconnectionDelay;
	}

	/**
	 * Sets HTTP collector reconnection delay.
	 *
	 * @param delay new delay in milliseconds.
	 */
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
