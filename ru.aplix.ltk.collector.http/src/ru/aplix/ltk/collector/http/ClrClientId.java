package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.collector.http.ClrProfileId.clrProfileId;

import java.util.UUID;


public class ClrClientId {

	public static ClrClientId clrClientId(String path) {
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
			providerProfile = ids.substring(0, slashIdx);
			uuid = UUID.fromString(ids.substring(slashIdx + 1));
		}

		return new ClrClientId(clrProfileId(providerProfile), uuid);
	}

	private final ClrProfileId profileId;
	private final UUID uuid;

	public ClrClientId(ClrProfileId profileId, UUID uuid) {
		this.profileId = profileId;
		this.uuid = uuid;
	}

	public final String getProviderId() {
		return getProfileId().getProviderId();
	}

	public final ClrProfileId getProfileId() {
		return this.profileId;
	}

	public final UUID getUUID() {
		return this.uuid;
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

}
