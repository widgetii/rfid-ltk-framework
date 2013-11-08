package ru.aplix.ltk.collector.http;

import static java.util.Objects.requireNonNull;
import ru.aplix.ltk.core.RfProvider;


/**
 * An identifier of HTTP collector profile.
 */
public final class ClrProfileId implements Comparable<ClrProfileId> {

	/**
	 * Construct the profile identifier from the given string representation.
	 *
	 * <p>The string representation of profile may be one of:
	 * <ul>
	 * <li>{@code <id> '@' <providerId>} - containing both profile's identifier
	 * and provider's one, or</li>
	 * <li>{@code <providerId>} - containing only profile's identifier; in this
	 * case the profile's identifier is empty string.</li>
	 * </ul>
	 * </p>
	 *
	 * @param id a string representation of profile identifier.
	 *
	 * @return reconstructed profile identifier.
	 */
	public static ClrProfileId clrProfileId(String id) {

		final int atIdx = id.indexOf('@');

		if (atIdx < 0) {
			return new ClrProfileId(id, "");
		}

		return new ClrProfileId(
				id.substring(atIdx + 1),
				id.substring(0, atIdx));
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

	@Override
	public int compareTo(ClrProfileId o) {

		final int providerIdCmp = this.providerId.compareTo(o.providerId);

		if (providerIdCmp != 0) {
			return providerIdCmp;
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

}
