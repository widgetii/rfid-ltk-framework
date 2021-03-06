package ru.aplix.ltk.collector.http;

import static java.util.Objects.requireNonNull;
import static ru.aplix.ltk.collector.http.ClrProfileId.urlDecodeClrProfileId;

import java.io.UnsupportedEncodingException;
import java.util.UUID;


/**
 * An identifier of HTTP collector client.
 */
public class ClrClientId {

	/**
	 * Constructs an identifier from the given URL-encoded path.
	 *
	 * <p>The path should contain a fragment with a
	 * {@link ClrProfileId#urlDecodeClrProfileId(String) profile identifier},
	 * and optionally - a client UUID, separated from profile identifier with
	 * a {@code '/'} sign.</p>
	 *
	 * <p>The path itself may start with a {@code '/'} sign, and may contain
	 * more fragments after the client UUID, separated from the latter by
	 * the {@code '/'} sign.</p>
	 *
	 * @param path {@link #urlEncode() URL-encoded} path containing identifier.
	 *
	 * @return new client identifier, or <code>null</code> if {@code path}
	 * is null.
	 */
	public static ClrClientId urlDecodeClrClientId(String path) {
		if (path == null) {
			return null;
		}

		final String rest;

		if (path.startsWith("/")) {
			rest = path.substring(1);
		} else {
			rest = path;
		}

		final String ids;
		final int slashIdx = rest.indexOf('/');

		if (slashIdx < 0) {
			ids = rest;
		} else {

			final int secondSlashIdx = rest.indexOf('/', slashIdx + 1);

			if (secondSlashIdx < 0) {
				ids = rest;
			} else {
				ids = rest.substring(0, secondSlashIdx);
			}
		}

		final UUID uuid;
		final String providerProfile;

		if (slashIdx < 0) {
			providerProfile = ids;
			uuid = null;
		} else {

			final int lastSlashIdx = ids.indexOf('/', slashIdx + 1);
			final String uuidStr =
					lastSlashIdx < 0
					? ids.substring(slashIdx + 1)
					: ids.substring(slashIdx + 1, lastSlashIdx);

			providerProfile = ids.substring(0, slashIdx);
			uuid = UUID.fromString(uuidStr);
		}

		return new ClrClientId(urlDecodeClrProfileId(providerProfile), uuid);
	}

	private final ClrProfileId profileId;
	private final UUID uuid;

	/**
	 * Constructs an identifier of HTTP collector client.
	 *
	 * @param profileId identifier of HTTP collector profile.
	 * @param uuid client UUID, or <code>null</code> if unknown.
	 */
	public ClrClientId(ClrProfileId profileId, UUID uuid) {
		requireNonNull(profileId, "RFID profile identifier not specified");
		this.profileId = profileId;
		this.uuid = uuid;
	}

	/**
	 * RFID provider identifier.
	 *
	 * @return {@link ClrProfileId#getProviderId() provider identifier}.
	 */
	public final String getProviderId() {
		return getProfileId().getProviderId();
	}

	/**
	 * Identifier of HTTP collector profile.
	 *
	 * @return identifier passed to the constructor.
	 */
	public final ClrProfileId getProfileId() {
		return this.profileId;
	}

	/**
	 * Client's UUID
	 *
	 * @return UUID passed to the constructor, or <code>null</code> if not
	 * specified.
	 */
	public final UUID getUUID() {
		return this.uuid;
	}

	/**
	 * URL-encodes this identifier.
	 *
	 * @return URL-encoded profile string, which can be included into URL.
	 */
	public final String urlEncode() {
		try {
			return urlEncode("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 is not supported", e);
		}
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + this.profileId.hashCode();
		result =
				prime * result
				+ ((this.uuid == null) ? 0 : this.uuid.hashCode());

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

		final ClrClientId other = (ClrClientId) obj;

		if (!this.profileId.equals(other.profileId)) {
			return false;
		}
		if (this.uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!this.uuid.equals(other.uuid)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		if (this.profileId == null) {
			return super.toString();
		}
		if (this.uuid == null) {
			return this.profileId.toString();
		}
		return this.profileId.toString() + '/' + this.uuid;
	}

	private String urlEncode(
			String encoding)
	throws UnsupportedEncodingException {

		final String profileId = this.profileId.urlEncode(encoding);

		if (getUUID() == null) {
			return profileId;
		}

		return profileId + '/' + getUUID().toString();
	}

}
