package ru.aplix.ltk.collector.http.server;

import static ru.aplix.ltk.collector.http.server.ClrProfileConfig.PROFILES_FILTER;
import static ru.aplix.ltk.collector.http.server.ClrProfileConfig.PROPERTIES_SUFFIX;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.osgi.Logger;


public final class ProviderClrProfiles<S extends RfSettings>
		implements Iterable<ClrProfile<S>> {

	private final AllClrProfiles allProfiles;
	private final RfProvider<S> provider;
	private final ConcurrentHashMap<String, ClrProfile<S>> profiles =
			new ConcurrentHashMap<>();

	ProviderClrProfiles(AllClrProfiles allProfiles, RfProvider<S> provider) {
		this.allProfiles = allProfiles;
		this.provider = provider;
	}

	public final AllClrProfiles allProfiles() {
		return this.allProfiles;
	}

	public final RfProvider<S> getProvider() {
		return this.provider;
	}

	public final Logger log() {
		return allProfiles().log();
	}

	public final ClrProfile<S> get(String id) {
		return this.profiles.get(id);
	}

	public void load() {

		final File configDir = configDir(false);

		if (configDir == null) {
			return;
		}
		for (File file : configDir.listFiles(PROFILES_FILTER)) {
			loadProfile(file);
		}
	}


	private File configDir(boolean createIfNotExist) {

		final String providerId = getProvider().getId();
		final File configDir =
				new File(allProfiles().getConfigDir(), providerId);

		if (!configDir.isDirectory()) {
			if (createIfNotExist && !configDir.mkdirs()) {
				throw new IllegalStateException(
						"Failed to create directory " + configDir);
			}
			log().debug("Provider directory does not exist " + providerId);
			// No profiles for this provider.
			return null;
		}

		return configDir;
	}

	@Override
	public Iterator<ClrProfile<S>> iterator() {
		return this.profiles.values().iterator();
	}

	public ClrProfile<S> createProfile(ClrProfileId profileId) {

		final ClrProfileConfig<S> config = new ClrProfileConfig<>(
				getProvider(),
				new File(
						configDir(true),
						profileId.getId() + PROPERTIES_SUFFIX));

		return new ClrProfile<>(this, config);
	}

	public void dispose() {

		final Iterator<ClrProfile<S>> it =
				this.profiles.values().iterator();

		while (it.hasNext()) {
			it.next().dispose();
			it.remove();
		}
	}

	ClrProfile<S> put(ClrProfile<S> profile) {

		final ClrProfile<S> old =
				this.profiles.put(profile.getProfileId().getId(), profile);

		if (old != null) {
			old.dispose();
		}

		return old;
	}

	void remove(ClrProfile<S> profile) {

		final ClrProfile<S> removed =
				this.profiles.remove(profile.getProfileId().getId());

		if (removed != null) {
			removed.dispose();
		}
	}

	private void loadProfile(File file) {

		final ClrProfileConfig<S> config =
				new ClrProfileConfig<>(getProvider(), file);
		final ClrProfileId profileId = config.getProfileId();
		final ClrProfile<S> profile = new ClrProfile<>(this, config);
		final ClrProfile<S> existing =
				this.profiles.put(profileId.getId(), profile);

		if (existing != null) {
			this.profiles.put(profileId.getId(), existing);
			log().error("Profile `" + profileId + "` already registered");
			return;
		}

		log().debug("Load profile `" + profileId + "` config from " + file);
		try {
			config.load();
		} catch (Throwable e) {
			log().error("Failed to load profile `" + profileId + "`", e);
			this.profiles.remove(profileId);
		}

		try {
			profile.init();
		} catch (Throwable e) {
			log().error(
					"Failed to initialize profile " + profile.getProfileId(),
					e);
		}
	}

}
