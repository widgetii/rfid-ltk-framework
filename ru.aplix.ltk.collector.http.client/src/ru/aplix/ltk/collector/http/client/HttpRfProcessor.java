package ru.aplix.ltk.collector.http.client;

import java.util.UUID;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;


/**
 * HTTP collector's client processing service.
 *
 * <p>This is an OSGi service which should be informed about incoming requests
 * from HTTP collector(s). It, in turn, informs the clients and their
 * subscribers. It can be informed by the servlet or Spring controller, which
 * {@code HttpRfSettings#getClientURL() address} is passed to the collector.</p>
 */
public interface HttpRfProcessor {

	/**
	 * Informs the client about RFID status update.
	 *
	 * @param clientUUID an UUID of the client to inform.
	 * @param status status update received.
	 */
	void updateStatus(UUID clientUUID, RfStatusRequest status);

	/**
	 * Informs the client about the change in RFID tag appearance.
	 *
	 * @param clientUUID an UUID of the client to inform.
	 * @param tagAppearance a change in tag appearance received.
	 */
	void updateTagAppearance(
			UUID clientUUID,
			RfTagAppearanceRequest tagAppearance);

	/**
	 * Inform the client that ping received.
	 *
	 * @param clientUUID an UUID of the client to inform.
	 */
	void ping(UUID clientUUID);

}
