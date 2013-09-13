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
	 * <p>RFID connection interface could be specific to the provider, but this
	 * is not generally required.</p>
	 *
	 * @param settings RFID settings.
	 *
	 * @return new connection.
	 */
	RfConnection connect(S settings);

}
