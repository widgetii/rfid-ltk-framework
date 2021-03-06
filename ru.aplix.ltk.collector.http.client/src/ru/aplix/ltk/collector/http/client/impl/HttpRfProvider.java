package ru.aplix.ltk.collector.http.client.impl;

import java.util.Hashtable;
import java.util.UUID;

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
	private BundleContext context;

	HttpRfProvider() {
		this.client = new HttpRfClient(this);
	}

	public final BundleContext getContext() {
		return this.context;
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
	public void updateStatus(UUID clientUUID, RfStatusRequest status) {
		this.client.getConnection(clientUUID).updateStatus(status);
	}

	@Override
	public void updateTagAppearance(
			UUID clientUUID,
			RfTagAppearanceRequest tagAppearance) {
		this.client.getConnection(clientUUID)
		.updateTagAppearance(tagAppearance);
	}

	@Override
	public void ping(UUID clientUUID) {
		this.client.getConnection(clientUUID).ping();
	}

	void register(BundleContext context) {
		this.context = context;

		final Hashtable<String, String> properties = new Hashtable<>(1);

		properties.put(RF_PROVIDER_ID, getId());

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
			this.context = null;
			this.registration.unregister();
			this.registration = null;
		}
	}

}
