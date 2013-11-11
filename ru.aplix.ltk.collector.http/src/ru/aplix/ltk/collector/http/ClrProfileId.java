package ru.aplix.ltk.collector.http;

import static java.util.Objects.requireNonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import ru.aplix.ltk.core.RfProvider;


/**
 * An identifier of HTTP collector profile.
 */
public final class ClrProfileId implements Comparable<ClrProfileId> {

	/**
	 * Construct the profile identifier from the given URL-encoded path.
	 *
	 * @param path an {@link #urlEncode() URL-encoded} representation of
	 * profile identifier.
	 *
	 * @return reconstructed profile identifier.
	 */
	public static ClrProfileId urlDecodeClrProfileId(String path) {
		try {
			return urlDecodeClrProfileId(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 is not supported", e);
		}
	}

	private static ClrProfileId urlDecodeClrProfileId(
			String path,
			String encoding)
	throws UnsupportedEncodingException {

		final int atIdx = path.indexOf('@');

		if (atIdx < 0) {
			return new ClrProfileId(URLDecoder.decode(path, encoding), "");
		}

		return new ClrProfileId(
				URLDecoder.decode(path.substring(atIdx + 1), encoding),
				URLDecoder.decode(path.substring(0, atIdx), encoding));
	}

	private final String providerId;
	private final String id;

	/**
	 * Constructs an identifier of HTTP collector profile.
	 *
	 * @param providerId provider identifier.
	 * @param id local profile id, or <code>null</code> to use an empty string.
	 */
	public ClrProfileId(String providerId, String id) {
		requireNonNull(providerId, "RFID provider identifier not specified");
		this.providerId = providerId;
		this.id = id != null ? id : "";
	}

	/**
	 * RFID provider identifier.
	 *
	 * <p>This identifier should match the target {@link RfProvider#getId()
	 * provider's one}.<p>
	 *
	 * @return identifier passed to the constructor.
	 */
	public final String getProviderId() {
		return this.providerId;
	}

	/**
	 * Local profile identifier.
	 *
	 * @return identifier passed to the constructor, or empty string.
	 */
	public final String getId() {
		return this.id;
	}

	/**
	 * Whether this is a default provider profile.
	 *
	 * @return <code>true</code> if {@link #getId() local} identifier is
	 * empty, or <code>false</code> otherwise.
	 */
	public final boolean isDefault() {
		return getId().isEmpty();
	}

	/**
	 * URL-encodes this identifier.
	 *
	 * <p>The URL-encoded representation of profile identifier may be one of:
	 * <ul>
	 * <li>{@code <id> '@' <providerId>} - containing both profile's identifier
	 * and provider's one, or</li>
	 * <li>{@code <providerId>} - containing only profile's identifier; in this
	 * case the profile's identifier is an empty string.</li>
	 * </ul>
	 * </p>
	 *
	 * @return URL-encoded profile string, which can be included into URL.
	 */
	public String urlEncode() {
		try {
			return urlEncode("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 is not supported", e);
		}
	}

	@Override
	public int compareTo(ClrProfileId o) {

		final int providerIdCmp = this.providerId.compareTo(o.providerId);

		if (providerIdCmp != 0) {
			return providerIdCmp;
		}
		if (isDefault()) {
			if (o.isDefault()) {
				return 0;
			}
			return 1;
		}
		if (o.isDefault()) {
			return -1;
		}

		return this.id.compareTo(o.id);
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + this.id.hashCode();
		result = prime * result + this.providerId.hashCode();

		return result;
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

		final ClrProfileId other = (ClrProfileId) obj;

		if (!this.id.equals(other.id)) {
			return false;
		}
		if (!this.providerId.equals(other.providerId)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		if (this.id == null) {
			return super.toString();
		}
		if (this.id.isEmpty()) {
			return this.providerId;
		}
		return this.id + '@' + this.providerId;
	}

	final String urlEncode(
			String encoding)
	throws UnsupportedEncodingException {

		final String providerId = URLEncoder.encode(getProviderId(), encoding);

		if (getId().isEmpty()) {
			return providerId;
		}

		return URLEncoder.encode(getId(), encoding) + '@' + providerId;
	}

}
