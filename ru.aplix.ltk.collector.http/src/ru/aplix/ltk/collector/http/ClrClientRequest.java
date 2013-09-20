package ru.aplix.ltk.collector.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


/**
 * The HTTP request to send to HTTP collector to subscribe to RFID messages.
 *
 * <p>The {@link #getClientURL() client URL} is mandatory.</p>
 *
 * <p>This request should be sent by GET request to HTTP collector. The URL
 * should contain a {@link ClrClientId#clrClientId(String) client identifier
 * path} after the collector's servlet path.</p>
 *
 * <p>Upon reception, the HTTP collector will start sending data to the client,
 * so the client should be ready to receive it <u>before</u> it initiates this
 * request.</p>
 */
public class ClrClientRequest implements Parameterized {

	private URL clientURL;

	/**
	 * Constructs an empty request.
	 */
	public ClrClientRequest() {
	}

	/**
	 * Reconstructs the request from the given parameters.
	 *
	 * @param parameters parameters containing the constructed request's data.
	 */
	public ClrClientRequest(Parameters parameters) {
		read(parameters);
	}

	/**
	 * HTTP collector's client URL.
	 *
	 * <p>This is an URL the HTTP collector will send HTTP requests with RFID
	 * messages to.</p>
	 *
	 * @return client URL.
	 */
	public URL getClientURL() {
		return this.clientURL;
	}

	/**
	 * Assigns client URL.
	 *
	 * <p>This option is mandatory.</p>
	 *
	 * @param clientURL new client URL.
	 */
	public void setClientURL(URL clientURL) {
		ensureHttpURL(clientURL);
		this.clientURL = clientURL;
	}

	@Override
	public void read(Parameters params) {
		setClientURL(clientURLFromParams(params));
	}

	@Override
	public void write(Parameters params) {
		params.set("clientURL", getClientURL());
	}

	private void ensureHttpURL(URL clientURL) {
		if (clientURL != null) {

			final String protocol = clientURL.getProtocol();

			if (!"http".equals(protocol) && !"https".equals(protocol)) {
				throw new IllegalArgumentException(
						"HTTP/HTTPS client URL expected: " + clientURL);
			}
		}
	}

	private URL clientURLFromParams(Parameters params) {
		try {

			final String url = params.valueOf(
					"clientURL",
					null,
					Objects.toString(getClientURL(), null));

			return url != null ? new URL(url) : null;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid client URL", e);
		}
	}

}
