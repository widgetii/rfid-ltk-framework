package ru.aplix.ltk.collector.http;

import org.osgi.service.http.HttpService;


/**
 * Collector HTTP service.
 */
public interface CollectorHttpService {

	/**
	 * The identifier of {@code HttpService} instance serving to this one.
	 */
	String COLLECTOR_HTTP_SERVICE_ID =
			CollectorHttpService.class.getPackage().getName();

	/**
	 * Returns HTTP service serving this one.
	 *
	 * @return {@code HttpService} instance.
	 */
	HttpService getHttpService();

}
