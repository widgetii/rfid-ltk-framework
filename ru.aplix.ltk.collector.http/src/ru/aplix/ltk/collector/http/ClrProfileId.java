package ru.aplix.ltk.collector.http;

import static java.util.Objects.requireNonNull;


public final class ClrProfileId {

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

	public ClrProfileId(String providerId) {
		this(providerId, "");
	}

	public ClrProfileId(String providerId, String id) {
		requireNonNull(providerId, "RFID provider identifier not specified");
		this.providerId = providerId;
		this.id = id != null ? id : "";
	}

	public final String getProviderId() {
		return this.providerId;
	}

	public final String getId() {
		return this.id;
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
