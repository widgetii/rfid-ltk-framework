package ru.aplix.ltk.collector.http.client;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


/**
 * Represents HTTP RFID collector server.
 */
public interface HttpRfServer {

	/**
	 * The root URL of HTTP RFID collector server.
	 *
	 * @return http/https URL.
	 */
	URL getServerURL();

	/**
	 * Creates new HTTP RFID profile.
	 *
	 * <p>To actually create it on the server, invoke its
	 * {@link HttpRfProfile#updateSettings(ClrProfileSettings)} method.
	 * </p>
	 *
	 * @param provider RFID provider the profile should use. This provider
	 * should exist at the server.
	 * @param profileId the {@link ClrProfileId#getId() local part} of profile
	 * identifier to create.
	 *
	 * @return new profile instance.
	 */
	<S extends RfSettings> HttpRfProfile<S> newProfile(
			RfProvider<S> provider,
			String profileId);

	/**
	 * Loads profiles from server.
	 *
	 * @return map of available HTTP RFID profiles by their identifiers.
	 *
	 * @throws IOException in case of communication failures, or if the server
	 * at the given URL is not a HTTP RFID server.
	 */
	Map<ClrProfileId, HttpRfProfile<?>> loadProfiles() throws IOException;

}
