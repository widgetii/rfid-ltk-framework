package ru.aplix.ltk.collector.http.client;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.impl.HttpRfClient;
import ru.aplix.ltk.collector.http.client.impl.HttpRfConnection;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;


public class HttpRfProvider implements RfProvider<HttpRfSettings> {

	private final HttpRfClient client;

	public HttpRfProvider() {
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
	public RfConnection connect(HttpRfSettings settings) {
		return this.client.connect(settings);
	}

	public void updateStatus(
			String clientPath,
			RfStatusRequest status) {

		final HttpRfConnection con = this.client.getConnection(clientPath);

		if (con != null) {
			con.updateStatus(status);
		}
	}

	public void updateTagAppearance(
			String clientPath,
			RfTagAppearanceRequest data) {

		final HttpRfConnection con = this.client.getConnection(clientPath);

		if (con != null) {
			con.updateTagAppearance(data);
		}
	}

	public void ping(String clientPath) {

		final HttpRfConnection con = this.client.getConnection(clientPath);

		if (con != null) {
			con.ping();
		}
	}

	public void dispose() {
		this.client.shutdown();
	}

}
