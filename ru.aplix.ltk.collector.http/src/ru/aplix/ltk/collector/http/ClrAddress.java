package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.collector.http.ClrClientId.urlDecodeClrClientId;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;


/**
 * The address of HTTP RFID collector server.
 *
 * <p>May also address one of its components.</p>
 */
public final class ClrAddress {

	/**
	 * The relative path to collector servlet, clients communicate with.
	 */
	public static final String COLLECTOR_SERVLET_PATH = "/collector";

	/**
	 * The relative path to profiles management servlet.
	 */
	public static final String PROFILES_SERVLET_PATH = "/profiles";

	private final URL url;
	private URL serverURL;
	private ClrClientId clientId;

	/**
	 * Constructs the address.
	 *
	 * @param url http(s) URL of RFID HTTP server or one of its components.
	 *
	 * @throws IllegalArgumentException if the given URL is not an http(s) one.
	 */
	public ClrAddress(URL url) {

		final String protocol = url.getProtocol();

		if (!"http".equals(protocol) && !"https".equals(protocol)) {
			throw new IllegalArgumentException(
					"Not an http(s) URL: " + url + " (" + protocol + ')');
		}

		this.url = url;
	}

	/**
	 * Original URL.
	 *
	 * @return the URL passed to the constructor.
	 */
	public final URL getURL() {
		return this.url;
	}

	/**
	 * Server URL.
	 *
	 * @return the root URL of the server.
	 */
	public final URL getServerURL() {
		if (this.serverURL != null) {
			return this.serverURL;
		}
		return this.serverURL = serverURL(getURL());
	}

	/**
	 * Client identifier.
	 *
	 * @return the identifier of the client from the {@link #getURL() original
	 * URL}, or <code>null</code> if it does not contain such identifier.
	 */
	public final ClrClientId getClientId() {
		if (this.clientId != null) {
			return this.clientId;
		}

		final String path = getURL().getPath();
		final String serverPath = getServerURL().getPath();
		final String pathInfo = path.substring(serverPath.length());

		return this.clientId = urlDecodeClrClientId(pathInfo);
	}

	/**
	 * Collector profile identifier.
	 *
	 * @return the identifier of collector profile from the {@link #getURL()
	 * original URL}, or <code>null</code> if it does not contain such
	 * identifier.
	 */
	public final ClrProfileId getProfileId() {

		final ClrClientId clientId = getClientId();

		return clientId != null ? clientId.getProfileId() : null;
	}

	/**
	 * Collector URL.
	 *
	 * <p>This is an address clients should communicate with.</p>
	 *
	 * @return collector servlet URL.
	 */
	public final URL getCollectorURL() {
		try {
			return new URL(getServerURL(), COLLECTOR_SERVLET_PATH);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(
					"Can not construct collector servlet URL",
					e);
		}
	}

	/**
	 * Profiles management URL.
	 *
	 * @return profiles management servlet URL.
	 *
	 * @see #PROFILES_SERVLET_PATH
	 */
	public final URL getProfilesURL() {
		try {
			return new URL(getServerURL(), PROFILES_SERVLET_PATH);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(
					"Can not construct profiles servlet URL",
					e);
		}
	}

	/**
	 * Constructs collector profile client URL.
	 *
	 * @param profileId profile identifier
	 *
	 * @return constructed URL.
	 */
	public final URL clientURL(ClrProfileId profileId) {

		final URL collectorURL = getCollectorURL();

		try {
			return new URL(
					collectorURL,
					collectorURL.getPath() + '/' + profileId.urlEncode());
		} catch (MalformedURLException e) {
			throw new IllegalStateException(
					"Can not construct client URL: " + profileId,
					e);
		}
	}

	/**
	 * Constructs collector client URL.
	 *
	 * @param clientId client identifier
	 *
	 * @return constructed URL.
	 */
	public final URL clientURL(ClrClientId clientId) {

		final UUID uuid = clientId.getUUID();

		if (uuid == null) {
			return clientURL(clientId.getProfileId());
		}

		final URL collectorURL = getCollectorURL();

		try {
			return new URL(
					collectorURL,
					collectorURL.getPath()
					+ '/' + clientId.urlEncode()
					+ '/' + uuid.toString());
		} catch (MalformedURLException e) {
			throw new IllegalStateException(
					"Can not construct client URL: " + clientId,
					e);
		}
	}

	/**
	 * Constructs profile management URL.
	 *
	 * @param profileId profile identifier to manage.
	 *
	 * @return constructed URL.
	 */
	public final URL profileURL(ClrProfileId profileId) {

		final URL profilesURL = getProfilesURL();

		try {
			return new URL(
					profilesURL,
					profilesURL.getPath() + '/' + profileId.urlEncode());
		} catch (MalformedURLException e) {
			throw new IllegalStateException(
					"Can not construct profile management URL: " + profileId,
					e);
		}
	}

	@Override
	public int hashCode() {
		return this.url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final ClrAddress other = (ClrAddress) obj;

		return this.url.equals(other.url);
	}

	@Override
	public String toString() {
		if (this.url == null) {
			return super.toString();
		}
		return this.url.toString();
	}

	private static URL serverURL(URL url) {

		final String path = url.getPath();
		final String serverPath = serverPath(path);

		if (serverPath == path) {
			return url;
		}

		try {
			return new URL(url, serverPath);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(
					"Can not construct collector server URL",
					e);
		}
	}

	private static String serverPath(String path) {

		final String woCollector = serverPath(path, COLLECTOR_SERVLET_PATH);

		if (woCollector != path) {
			return woCollector;
		}

		return serverPath(path, PROFILES_SERVLET_PATH);
	}

	private static String serverPath(String path, String servletPath) {
		if (path.endsWith(servletPath)) {
			return path.substring(
					0,
					path.length() - servletPath.length());
		}

		final int servletIdx = path.indexOf(servletPath + '/');

		if (servletIdx >= 0) {
			return path.substring(0, servletIdx);
		}

		return removeTrailingSlash(path);
	}

	private static String removeTrailingSlash(String path) {
		if (!path.endsWith("/")) {
			return path;
		}
		return removeTrailingSlash(path.substring(0, path.length() - 1));
	}

}
