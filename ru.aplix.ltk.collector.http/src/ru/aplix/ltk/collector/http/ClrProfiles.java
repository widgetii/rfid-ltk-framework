package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.collector.http.ClrProfileId.parseClrProfileId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfProvidersResolver;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


/**
 * All profiles existing at HTTP RFID collector server.
 */
public class ClrProfiles implements Parameterized {

	private final RfProvidersResolver providerResolver;
	private final HashMap<ClrProfileId, ClrProfileSettings<?>> profiles =
			new HashMap<>();

	/**
	 * Constructs profiles without RFID providers resolver.
	 */
	public ClrProfiles() {
		this.providerResolver = null;
	}

	/**
	 * Constructs profiles.
	 *
	 * @param providerResolver RFID providers resolver. Required for
	 * deserialization.
	 */
	public ClrProfiles(RfProvidersResolver providerResolver) {
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
	public final Map<ClrProfileId, ClrProfileSettings<?>> profiles() {
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

			final ClrProfileSettings<?> profileData =
					profileSettings(profileId);

			if (profileData == null) {
				continue;
			}

			profileData.read(params.sub(profileId.toString()));
		}
	}

	@Override
	public void write(Parameters params) {
		for (Map.Entry<ClrProfileId, ClrProfileSettings<?>> e :
			profiles().entrySet()) {

			final ClrProfileId profileId = e.getKey();
			final ClrProfileSettings<?> profile = e.getValue();
			final Parameters profileParams = params.sub(profileId.toString());

			profile.write(profileParams);
		}
	}

	private ClrProfileSettings<?> profileSettings(ClrProfileId profileId) {

		final ClrProfileSettings<?> existing = this.profiles.get(profileId);

		if (existing != null) {
			return existing;
		}

		final RfProvider<?> provider =
				getProviderResolver().rfProviderById(profileId.getProviderId());

		if (provider == null) {
			return null;
		}

		final ClrProfileSettings<?> profileSettings =
				new ClrProfileSettings<>(provider);

		this.profiles.put(profileId, profileSettings);

		return profileSettings;
	}

	private static ClrProfileId profileId(String name) {

		final int dot = name.indexOf('.');

		if (dot < 0) {
			return null;
		}

		return parseClrProfileId(name.substring(0, dot));
	}

}
