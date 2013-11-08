package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.collector.http.ClrProfileId.clrProfileId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfProvidersResolver;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


/**
 * The data of all profiles existing at collector server.
 */
public class ClrProfilesData implements Parameterized {

	private final RfProvidersResolver providerResolver;
	private final HashMap<ClrProfileId, ClrProfileData<?>> profiles =
			new HashMap<>();

	/**
	 * Constructs profiles data without RFID providers resolver.
	 */
	public ClrProfilesData() {
		this.providerResolver = null;
	}

	/**
	 * Constructs profiles data.
	 *
	 * @param providerResolver RFID providers resolver. Required for
	 * deserialization.
	 */
	public ClrProfilesData(RfProvidersResolver providerResolver) {
		this.providerResolver = providerResolver;
	}

	/**
	 * RFID providers resolver.
	 *
	 * @return the resolver passed to the constructor, or <code>null</code>.
	 */
	public final RfProvidersResolver getProviderResolver() {
		return this.providerResolver;
	}

	/**
	 * Collector profiles.
	 *
	 * @return a map of collector profiles by their identifiers.
	 */
	public final Map<ClrProfileId, ClrProfileData<?>> profiles() {
		return this.profiles;
	}

	@Override
	public void read(Parameters params) {

		final HashSet<ClrProfileId> readProfiles = new HashSet<>();

		for (String name : params.store()) {

			final ClrProfileId profileId = profileId(name);

			if (profileId == null || !readProfiles.add(profileId)) {
				continue;
			}

			final ClrProfileData<?> profileData = profileData(profileId);

			if (profileData == null) {
				continue;
			}

			profileData.read(params.sub(profileId.toString()));
		}
	}

	@Override
	public void write(Parameters params) {
		for (Map.Entry<ClrProfileId, ClrProfileData<?>> e :
			profiles().entrySet()) {

			final ClrProfileId id = e.getKey();
			final ClrProfileData<?> profile = e.getValue();
			final Parameters profileParams = params.sub(id.toString());

			profile.write(profileParams);
		}
	}

	private ClrProfileData<?> profileData(ClrProfileId profileId) {

		final ClrProfileData<?> existing = this.profiles.get(profileId);

		if (existing != null) {
			return existing;
		}

		final RfProvider<?> provider =
				getProviderResolver().rfProviderById(profileId.getId());

		if (provider == null) {
			return null;
		}

		final ClrProfileData<?> profileData = new ClrProfileData<>(provider);

		this.profiles.put(profileId, profileData);

		return profileData;
	}

	private static ClrProfileId profileId(String name) {

		final int dot = name.indexOf('.');

		if (dot < 0) {
			return null;
		}

		return clrProfileId(name.substring(0, dot));
	}

}
