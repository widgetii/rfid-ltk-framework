package ru.aplix.ltk.collector.http.client;

import ru.aplix.ltk.collector.http.ClrAddress;


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
	 * @param address the address of the server.
	 *
	 * @return HTTP RFID collector server representation.
	 */
	HttpRfServer httpRfServer(ClrAddress address);

}
