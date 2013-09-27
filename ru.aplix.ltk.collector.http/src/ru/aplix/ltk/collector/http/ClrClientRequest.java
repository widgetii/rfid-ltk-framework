package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.URL_PARAMETER_TYPE;

import java.net.URL;

import ru.aplix.ltk.core.util.Parameter;
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

	private static final Parameter<URL> CLIENT_URL =
			URL_PARAMETER_TYPE.parameter("clientURL");
	private static final Parameter<Long> LAST_TAG_EVENT_ID =
			LONG_PARAMETER_TYPE.parameter("lastTagEventId")
			.byDefault(0L);

	private URL clientURL;
	private long lastTagEventId = LAST_TAG_EVENT_ID.getDefault();

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

	/**
	 * Last tag appearance event identifier known to the client.
	 *
	 * @return event identifier, or non-positive number to request only new
	 * events.
	 */
	public long getLastTagEventId() {
		return this.lastTagEventId;
	}

	/**
	 * Sets the last tag appearance event identifier known to the client.
	 *
	 * @param eventId event identifier, or non-positive number to request only
	 * new events.
	 */
	public void setLastTagEventId(long eventId) {
		this.lastTagEventId = eventId;
	}

	@Override
	public void read(Parameters params) {
		setClientURL(params.valueOf(CLIENT_URL, getClientURL()));
		setLastTagEventId(params.valueOf(
				LAST_TAG_EVENT_ID,
				getLastTagEventId()));
	}

	@Override
	public void write(Parameters params) {
		params.set(CLIENT_URL, getClientURL());
		params.set(LAST_TAG_EVENT_ID, getLastTagEventId());
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

}
