package ru.aplix.ltk.core;


/**
 * RFID reader connector.
 *
 * <p>A connector can be constructed by {@link RfService#newConnector() service}
 * </p>
 *
 * <p>The connector can be used to configure a connection to the reader, and
 * then - for creating this connection.</p>
 *
 * <p>Concrete connector's interface can be specific to the service constructing
 * this connector. This way arbitrary configuration options could be provided
 * for the connection.</p>
 *
 * <p>Connectors are not expected to be thread-safe.</p>
 */
public interface RfConnector {

	/**
	 * Opens connection to RFID reader.
	 *
	 * <p>Reader connection interface could be specific to the service this
	 * connector is provided by. But this is not generally required.</p>
	 *
	 * @return new connection.
	 */
	RfConnection open();

}
