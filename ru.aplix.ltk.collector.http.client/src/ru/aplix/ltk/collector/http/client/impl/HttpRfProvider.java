package ru.aplix.ltk.collector.http.client.impl;

import static org.osgi.framework.Constants.SERVICE_ID;
import static ru.aplix.ltk.collector.http.client.HttpRfSettings.HTTP_RF_PROVIDER_ID;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.HttpRfProcessor;
import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;


final class HttpRfProvider
		implements RfProvider<HttpRfSettings>, HttpRfProcessor {

	private final HttpRfClient client;
	private ServiceRegistration<?> registration;

	HttpRfProvider() {
		this.client = new HttpRfClient();
	}

	@Override
	public String getId() {
		return "http";
	}

	@Override
	public String getName() {
		return "RFID over HTTP";
	}

	@Override
	public Class<? extends HttpRfSettings> getSettingsType() {
		return HttpRfSettings.class;
	}

	@Override
	public HttpRfSettings newSettings() {
		return new HttpRfSettings();
	}

	@Override
	public HttpRfSettings copySettings(HttpRfSettings settings) {
		return settings.clone();
	}

	@Override
	public RfConnection connect(HttpRfSettings settings) {
		return this.client.connect(settings);
	}

	@Override
	public void updateStatus(
			String clientPath,
			RfStatusRequest status) {

		final HttpRfConnection con = this.client.getConnection(clientPath);

		if (con != null) {
			con.updateStatus(status);
		}
	}

	@Override
	public void updateTagAppearance(
			String clientPath,
			RfTagAppearanceRequest data) {

		final HttpRfConnection con = this.client.getConnection(clientPath);

		if (con != null) {
			con.updateTagAppearance(data);
		}
	}

	@Override
	public void ping(String clientPath) {

		final HttpRfConnection con = this.client.getConnection(clientPath);

		if (con != null) {
			con.ping();
		}
	}

	void register(BundleContext context) {

		final Hashtable<String, String> properties = new Hashtable<>(1);

		properties.put(SERVICE_ID, HTTP_RF_PROVIDER_ID);

		this.registration = context.registerService(
				new String[] {
					RfProvider.class.getName(),
					HttpRfProcessor.class.getName(),
				},
				this,
				properties);
	}

	void unregister() {
		try {
			this.client.shutdown();
		} finally {
			this.registration.unregister();
			this.registration = null;
		}
	}

}
