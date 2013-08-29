package ru.aplix.ltk.core;


/**
 * RFID provider.
 *
 * <p>Provider instance cane be used to construct RFID reader connectors.</p>
 *
 * <p>Multiple such providers could be registered in OSGi. It is expected that
 * a concrete provider implement its own specific interface, and export it along
 * with a generic {@code RfProvider} one. This way providers consumers can
 * distinguish between different providers e.g. to construct an UI specific to
 * each provider.</p>
 */
public interface RfProvider {

	/**
	 * Provider's name.
	 *
	 * @return human-readable provider name.
	 */
	String getName();

	/**
	 * Creates a new RFID reader connector.
	 *
	 * <p>Arbitrary number of connectors can be constructed by single provider.
	 * </p>
	 *
	 * <p>A reader connector interface could be specific to concrete provider
	 * interface.</p>
	 *
	 * @return new reader connector.
	 */
	RfConnector newConnector();

}
