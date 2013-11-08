package ru.aplix.ltk.collector.http;

import static java.util.Objects.requireNonNull;
import static ru.aplix.ltk.core.util.ParameterType.BOOLEAN_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.STRING_PARAMETER_TYPE;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameter;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


/**
 * RFID collector's profile settings.
 *
 * <p>Collector server performs accordingly to profiles stored on it. These
 * profiles can be either preconfigured or managed remotely via dedicated
 * servlet. An instances of this class can be used for interchanging with that
 * servlet.</p>
 *
 * @param <S> RFID settings type.
 */
public class ClrProfileSettings<S extends RfSettings> implements Parameterized {

	/**
	 * Human-readable profile name.
	 */
	public static final Parameter<String> PROFILE_NAME =
			STRING_PARAMETER_TYPE.parameter("profileName");

	/**
	 * Automatic startup mode.
	 *
	 * <p>When <code>true</code>, an RFID collector will start collecting RFID
	 * data on startup. This is only useful for logging drivers, as the data
	 * they collect may be requested later.</p>
	 *
	 * <p><code>false</code> by default.</p>
	 */
	public static final Parameter<Boolean> AUTOSTART =
			BOOLEAN_PARAMETER_TYPE.parameter("autostart").byDefault(false);

	private final RfProvider<S> provider;
	private String profileName;
	private S settings;
	private boolean autostart;

	/**
	 * Constructs RFID collector's profile data.
	 *
	 * @param provider RFID provider of the profile.
	 */
	public ClrProfileSettings(RfProvider<S> provider) {
		requireNonNull(provider, "RFID provider not specified");
		this.provider = provider;
		this.settings = provider.newSettings();
	}

	/**
	 * RFID provider of collector's profile.
	 *
	 * @return the provider passed to constructor.
	 */
	public final RfProvider<S> getProvider() {
		return this.provider;
	}

	/**
	 * The name of this profile.
	 *
	 * @return human-readable name, or <code>null</code> if not set.
	 */
	public final String getProfileName() {
		return this.profileName;
	}

	/**
	 * Sets the name of this profile.
	 *
	 * @param profileName human-readable name, or <code>null</code>.
	 */
	public final void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 * RFID settings used by this profile.
	 *
	 * @return RFID settings.
	 */
	public final S getSettings() {
		return this.settings;
	}

	/**
	 * Sets RFID settings to use by this profile.
	 *
	 * @param settings new RFID settings, or <code>null</code> to set it to
	 * default values.
	 */
	public final void setSettings(S settings) {
		this.settings =
				settings != null ? settings : getProvider().newSettings();
	}

	/**
	 * Whether this profile should auto-start.
	 *
	 * @return <code>true</code> if collecting starts automatically on
	 * collector startup, or <code>false</code> if it only starts on demand.
	 *
	 * @see #AUTOSTART
	 */
	public final boolean isAutostart() {
		return this.autostart;
	}

	/**
	 * Sets this profiles automatic startup mode.
	 *
	 * @param autostart <code>true</code> to start collecting automatically on
	 * collector startup, or <code>false</code> to start it only on demand.
	 *
	 * @see #AUTOSTART
	 */
	public final void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	@Override
	public void read(Parameters params) {
		setProfileName(params.valueOf(PROFILE_NAME, getProfileName()));
		getSettings().read(params);
		setAutostart(params.valueOf(AUTOSTART, isAutostart()));
	}

	@Override
	public void write(Parameters params) {
		params.set(PROFILE_NAME, getProfileName());
		getSettings().write(params);
		if (isAutostart()) {
			params.set(AUTOSTART, true);
		}
	}

}
