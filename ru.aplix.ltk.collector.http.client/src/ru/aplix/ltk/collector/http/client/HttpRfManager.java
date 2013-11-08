package ru.aplix.ltk.collector.http.client;

import java.net.URL;


/**
 * HTTP RFID collector remote manager.
 *
 * <p>It is available as OSGi service, and can be used to create, update, or
 * delete collector profiles at the remote HTTP collector server.</p>
 */
public interface HttpRfManager {

	/**
	 * Creates new HTTP RFID collector server.
	 *
	 * @param url http/https URL of the collector server. This can be either
	 * root URL of the server, profile management servlet address, or collector
	 * servlet address.
	 *
	 * @return HTTP RFID collector server representation.
	 *
	 * @throws IllegalArgumentException if the given URL is not http or https
	 * one.
	 */
	HttpRfServer httpRfServer(URL url);

}
