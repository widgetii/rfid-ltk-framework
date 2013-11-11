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

		final String pathInfo = getPathInfo();

		if (pathInfo.isEmpty()) {
			return null;
		}

		return this.clientId = urlDecodeClrClientId(pathInfo);
	}

	/**
	 * Path.
	 *
	 * @return part of {@code #getURL() original URL} following the
	 * {@link #getServerURL() server URL}, including leading slash.
	 */
	public final String getPath() {

		final String path = getURL().getPath();
		final String serverPath = removeTrailingSlash(getServerURL().getPath());

		return path.substring(serverPath.length());
	}

	/**
	 * Path info after servlet path.
	 *
	 * @return path info without leading slash, or empty string.
	 */
	public final String getPathInfo() {

		final String path = getPath();
		final String info1 = pathInfo(COLLECTOR_SERVLET_PATH, path);

		if (info1 != null) {
			return info1;
		}

		final String info2 = pathInfo(PROFILES_SERVLET_PATH, path);

		if (info2 != null) {
			return info2;
		}

		return "";
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
		return serverURL(COLLECTOR_SERVLET_PATH);
	}

	/**
	 * Profiles management URL.
	 *
	 * @return profiles management servlet URL.
	 *
	 * @see #PROFILES_SERVLET_PATH
	 */
	public final URL getProfilesURL() {
		return serverURL(PROFILES_SERVLET_PATH);
	}

	/**
	 * Constructs a path on server.
	 *
	 * @param path path to append to {@link #getServerURL() server URL}.
	 * Can start with slash.
	 *
	 * @return constructed URL.
	 */
	public URL serverURL(String path) {

		final URL serverURL = getServerURL();
		final String serverPath = removeTrailingSlash(serverURL.getPath());
		final String result;

		if (path.startsWith("/")) {
			result = serverPath + path;
		} else {
			result = serverPath + '/' + path;
		}

		try {
			return new URL(serverURL, result);
		} catch (MalformedURLException e) {
			throw new RuntimeException(
					"Can not construct server URL (" + path + ')',
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
			return removeTrailingSlash(path.substring(
					0,
					path.length() - servletPath.length()));
		}

		final int servletIdx = path.indexOf(servletPath + '/');

		if (servletIdx >= 0) {
			if (servletIdx == 0) {
				return "/";
			}
			return removeTrailingSlash(path.substring(0, servletIdx));
		}

		return removeTrailingSlash(path);
	}

	private static String removeLeadingSlash(String path) {
		if (!path.startsWith("/")) {
			return path;
		}
		return removeLeadingSlash(path.substring(1));
	}

	private static String removeTrailingSlash(String path) {
		if (!path.endsWith("/")) {
			return path;
		}
		return removeTrailingSlash(path.substring(0, path.length() - 1));
	}

	private static String pathInfo(String servletPath, String path) {

		final String prefix = servletPath + '/';

		if (!path.startsWith(prefix)) {
			return null;
		}

		return removeLeadingSlash(path.substring(prefix.length()));
	}

}
