package ru.aplix.ltk.core;


/**
 * An interface responsible for RFID providers resolution by their identifiers.
 */
public interface RfProvidersResolver {

	/**
	 * Finds RFID provider by its identifier.
	 *
	 * @param providerId target provider's identifier.
	 *
	 * @return RFID provider with the given identifier, or <code>null</code> if
	 * no such provider found.
	 */
	RfProvider<?> rfProviderById(String providerId);

}
