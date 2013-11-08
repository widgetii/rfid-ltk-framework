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
	 * @return profile settings loaded from the server.
	 */
	ClrProfileSettings<S> getSettings();

	/**
	 * Updates profile settings.
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
