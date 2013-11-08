package ru.aplix.ltk.collector.http.server;

import static ru.aplix.ltk.collector.http.server.ClrProfileConfig.PROFILES_FILTER;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.osgi.Logger;


final class ProviderClrProfiles<S extends RfSettings>
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

		final String providerId = getProvider().getId();
		final File configDir =
				new File(allProfiles().getConfigDir(), providerId);

		if (!configDir.isDirectory()) {
			log().debug("No profiles found for provider " + providerId);
			// No profiles for this provider.
			return;
		}
		for (File file : configDir.listFiles(PROFILES_FILTER)) {
			loadProfile(file);
		}
	}

	@Override
	public Iterator<ClrProfile<S>> iterator() {
		return this.profiles.values().iterator();
	}

	public void dispose() {

		final Iterator<ClrProfile<S>> it =
				this.profiles.values().iterator();

		while (it.hasNext()) {
			it.next().dispose();
			it.remove();
		}
	}

	private void loadProfile(File file) {

		final ClrProfileConfig<S> config =
				new ClrProfileConfig<>(getProvider(), file);
		final ClrProfileId profileId = config.getProfileId();
		final ClrProfile<S> profile =
				new ClrProfile<>(allProfiles(), config);
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

		if (config.isAutostart()) {
			profile.autostart();
		}
	}

}
