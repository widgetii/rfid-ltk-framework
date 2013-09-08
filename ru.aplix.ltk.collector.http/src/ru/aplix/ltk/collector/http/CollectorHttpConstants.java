package ru.aplix.ltk.collector.http;


/**
 * Collector HTTP service constants.
 */
public interface CollectorHttpConstants {

	/**
	 * The identifier of collector {@code HttpService}.
	 */
	String COLLECTOR_HTTP_SERVICE_ID =
			CollectorHttpConstants.class.getPackage().getName();

	/**
	 * A path relative to {@link ClrClientRequest#getClientURL() client URL},
	 * to which {@link RfStatusRequest status updates} will be sent.
	 */
	String CLR_HTTP_CLIENT_STATUS_PATH = "status";

	/**
	 * A path relative to {@link ClrClientRequest#getClientURL() client URL},
	 * to which {@code GET} requests (pings) will be sent periodically.
	 */
	String CLR_HTTP_CLIENT_PING_PATH = "ping";

	/**
	 * A path relative to {@link ClrClientRequest#getClientURL() client URL},
	 * to which {@link RfTagAppearanceRequest tags} will be sent.
	 */
	String CLR_HTTP_CLIENT_TAG_PATH = "tag";

}
