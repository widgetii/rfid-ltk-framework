package ru.aplix.ltk.collector.http.client;

import java.io.IOException;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


/**
 * A representation of HTTP RFID collector's profile.
 *
 * @param <S> RFID settings type.
 */
public interface HttpRfProfile<S extends RfSettings> {

	/**
	 * RFID provider this profile is using.
	 *
	 * @return RFID provider.
	 */
	RfProvider<S> getProvider();

	/**
	 * Profile identifier.
	 *
	 * @return unique profile identifier.
	 */
	ClrProfileId getProfileId();

	/**
	 * Profile settings.
	 *
	 * @return profile settings loaded from the server, or <code>null</code> if
	 * settings not loaded yet.
	 */
	ClrProfileSettings<S> getSettings();

	/**
	 * Loads profile settings from collector server.
	 *
	 * <p>The {@link #getSettings()} method will return the loaded settings
	 * after successful load.</p>
	 *
	 * @return loaded profile settings.
	 *
	 * @throws IOException in case of communication errors.
	 */
	ClrProfileSettings<S> loadSettings() throws IOException;

	/**
	 * Updates profile settings.
	 *
	 * <p>The {@link #getSettings()} method will return the new settings after
	 * successful update.</p>
	 *
	 * @param settings profile settings to upload to server.
	 *
	 * @throws IOException in case of communication errors.
	 */
	void updateSettings(ClrProfileSettings<S> settings) throws IOException;

	/**
	 * Deletes the profile from the server.
	 *
	 * @throws IOException in case of communication errors.
	 */
	void delete() throws IOException;

}
