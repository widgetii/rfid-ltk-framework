package ru.aplix.ltk.collector.http.client.impl.manager;

import static ru.aplix.ltk.collector.http.client.impl.manager.HttpRfServerImpl.EMPTY_RESPONSE_HANDLER;
import static ru.aplix.ltk.collector.http.client.impl.manager.HttpRfServerImpl.PARAMETERS_RESPONSE_HANDLER;

import java.io.IOException;
import java.net.URL;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.ClrProfileSettings;
import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameters;


class HttpRfProfileImpl<S extends RfSettings> implements HttpRfProfile<S> {

	private final HttpRfServerImpl server;
	private final RfProvider<S> provider;
	private final ClrProfileId profileId;
	private ClrProfileSettings<S> settings;

	HttpRfProfileImpl(
			HttpRfServerImpl server,
			RfProvider<S> provider,
			ClrProfileId profileId,
			ClrProfileSettings<S> settings) {
		this.server = server;
		this.provider = provider;
		this.profileId = profileId;
		this.settings = settings;
	}

	public final HttpRfServerImpl getServer() {
		return this.server;
	}

	@Override
	public RfProvider<S> getProvider() {
		return this.provider;
	}

	@Override
	public ClrProfileId getProfileId() {
		return this.profileId;
	}

	@Override
	public ClrProfileSettings<S> getSettings() {
		return this.settings;
	}

	public URL profileURL() {
		return getServer().getAddress().profileURL(getProfileId());
	}

	@Override
	public ClrProfileSettings<S> loadSettings() throws IOException {

		final HttpGet get = new HttpGet(profileURL().toExternalForm());
		final Parameters parameters =
				getServer().getManager().httpClient().execute(
						get,
						PARAMETERS_RESPONSE_HANDLER);
		final ClrProfileSettings<S> settings =
				new ClrProfileSettings<>(getProvider());

		settings.read(parameters);

		return this.settings = settings;
	}

	@Override
	public void updateSettings(
			ClrProfileSettings<S> settings)
	throws IOException {

		final HttpPut put = new HttpPut(profileURL().toExternalForm());

		put.setEntity(new ParametersEntity(settings));

		getServer().getManager().httpClient().execute(
				put,
				EMPTY_RESPONSE_HANDLER);

		this.settings = settings;
	}

	@Override
	public void delete() throws IOException {

		final HttpDelete delete = new HttpDelete(profileURL().toExternalForm());

		getServer().getManager().httpClient().execute(
				delete,
				EMPTY_RESPONSE_HANDLER);

	}

}
