package ru.aplix.ltk.core;


/**
 * RFID OSGi service.
 *
 * <p>Service instance cane be used to construct RFID reader connectors.</p>
 *
 * <p>Multiple such services could be registered in OSGi. It is expected that
 * a concrete service implement its own interface, and export it along with a
 * generic {@code RfService} one. This way service consumers can
 * distinguish between different service implementations, e.g. to provide an UI specific
 * to service implementation.</p>
 */
public interface RfService {

	/**
	 * Creates a new RFID reader connector.
	 *
	 * <p>Arbitrary number of connector can be constructed by single service.
	 * </p>
	 *
	 * <p>A reader connector interface could be specific to concrete service
	 * interface.</p>
	 *
	 * @return new reader connector.
	 */
	RfConnector newConnector();

}
