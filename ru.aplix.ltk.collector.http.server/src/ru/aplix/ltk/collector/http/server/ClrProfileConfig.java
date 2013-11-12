package ru.aplix.ltk.collector.http.server;

import static ru.aplix.ltk.collector.http.ClrProfileSettings.AUTOSTART;
import static ru.aplix.ltk.collector.http.ClrProfileSettings.PROFILE_NAME;

import java.io.*;
import java.util.Properties;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameters;


final class ClrProfileConfig<S extends RfSettings> {

	static final String PROPERTIES_SUFFIX = ".properties";
	static final FilenameFilter PROFILES_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(PROPERTIES_SUFFIX) && !name.startsWith(".");
		}
	};

	private final RfProvider<S> provider;
	private final ClrProfileId profileId;
	private final File file;
	private final Properties properties;
	private final Parameters parameters;

	ClrProfileConfig(RfProvider<S> provider, File file) {
		this.provider = provider;
		this.profileId = profileId(provider, file);
		this.file = file;
		this.properties = new Properties();
		this.parameters = new Parameters(this.properties);
	}

	public final RfProvider<S> getProvider() {
		return this.provider;
	}

	public final ClrProfileId getProfileId() {
		return this.profileId;
	}

	public final File getFile() {
		return this.file;
	}

	public final boolean isAutostart() {
		return getParameters().valueOf(AUTOSTART, false);
	}

	public final String getProfileName() {
		return getParameters().valueOf(PROFILE_NAME);
	}

	public final Parameters getParameters() {
		return this.parameters;
	}

	public final S settings() {

		final RfProvider<S> provider = getProvider();
		final S settings = provider.newSettings();

		settings.read(getParameters());

		return settings;
	}

	public void load() throws IOException {
		try (FileInputStream in = new FileInputStream(this.file)) {
			this.properties.load(in);
		}
	}

	public void store() throws IOException {
		try (FileOutputStream out = new FileOutputStream(this.file)) {
			this.properties.store(out, "Collector profile");
		}
	}

	@Override
	public String toString() {
		if (this.profileId == null) {
			return super.toString();
		}
		return this.profileId.toString();
	}

	private static ClrProfileId profileId(RfProvider<?> provider, File config) {

		final String name = config.getName();
		final String id = name.substring(
				0,
				name.length() - PROPERTIES_SUFFIX.length());

		return new ClrProfileId(provider.getId(), id);
	}

}
