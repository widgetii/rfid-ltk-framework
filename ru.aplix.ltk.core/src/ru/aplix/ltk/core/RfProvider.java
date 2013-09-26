package ru.aplix.ltk.core;


/**
 * RFID provider.
 *
 * <p>Provider instance can be used to configure and open RFID connections.</p>
 *
 * <p>Multiple such providers could be registered in OSGi.</p>
 *
 * @param <S> RFID settings type.
 */
public interface RfProvider<S extends RfSettings> {

	/**
	 * {@code RfProvider} class instance.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	Class<RfProvider<?>> RF_PROVIDER_CLASS = (Class) RfProvider.class;

	/**
	 * OSGi service property name containing RFID provider identifier.
	 */
	String RF_PROVIDER_ID = "ru.aplix.ltk.provider.id";

	/**
	 * OSGi service property name containing a set of proxy identifiers created
	 * by this service.
	 *
	 * <p>This can be used to prevent the proxy to be created for the second
	 * time, and to prioritize proxy creation.</p>
	 *
	 * <p>The value should be a string, a collection (preferably set) of
	 * strings, or an array of strings.<p>
	 */
	String RF_PROVIDER_PROXY = "ru.aplix.ltk.provider.proxy";

	/**
	 * Provider's identifier.
	 *
	 * @return unique ASCII provider identifier.
	 */
	String getId();

	/**
	 * Provider's name.
	 *
	 * @return human-readable provider name.
	 */
	String getName();

	/**
	 * Supported RFID settings.
	 *
	 * @return supported RFID settings class.
	 */
	Class<? extends S> getSettingsType();

	/**
	 * Creates a new RFID settings.
	 *
	 * @return new settings instance.
	 */
	S newSettings();

	/**
	 * Copies RFID settings.
	 *
	 * @param settings settings to copy.
	 *
	 * @return new settings instance with values copied from {@code settings}.
	 */
	S copySettings(S settings);

	/**
	 * Opens RFID connection with the given settings.
	 *
	 * @param settings RFID settings for the connection.
	 *
	 * @return new connection.
	 */
	RfConnection connect(S settings);

}
