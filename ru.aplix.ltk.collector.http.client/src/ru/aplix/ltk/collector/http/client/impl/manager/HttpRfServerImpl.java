package ru.aplix.ltk.collector.http.client.impl.manager;

import static org.apache.http.util.EntityUtils.getContentCharSet;
import static org.apache.http.util.EntityUtils.getContentMimeType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import ru.aplix.ltk.collector.http.*;
import ru.aplix.ltk.collector.http.client.*;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameters;


final class HttpRfServerImpl implements HttpRfServer {

	public static final ResponseHandler<Void> EMPTY_RESPONSE_HANDLER =
			new ResponseHandler<Void>() {
				@Override
				public Void handleResponse(
						HttpResponse response)
				throws ClientProtocolException, IOException {
					checkResponse(response);
					EntityUtils.consume(response.getEntity());
					return null;
				}
			};

	private final HttpRfManagerImpl manager;
	private final ClrAddress address;

	HttpRfServerImpl(HttpRfManagerImpl manager, ClrAddress address) {
		this.manager = manager;
		this.address = address;
	}

	public final HttpRfManagerImpl getManager() {
		return this.manager;
	}

	@Override
	public final ClrAddress getAddress() {
		return this.address;
	}

	@Override
	public <S extends RfSettings> HttpRfProfile<S> newProfile(
			RfProvider<S> provider,
			String profileId) {

		final ClrProfileId id = new ClrProfileId(provider.getId(), profileId);

		if (id.getId().isEmpty()) {
			throw new IllegalArgumentException(
					"Invalid profile identifier: `" + id.getId() + "`");
		}

		return new HttpRfProfileImpl<>(this, provider, id, null);
	}

	@Override
	public Map<ClrProfileId, HttpRfProfile<?>> loadProfiles()
			throws IOException {

		final HttpGet get =
				new HttpGet(getAddress().getProfilesURL().toExternalForm());
		final ClrProfiles response = getManager().httpClient().execute(
				get,
				new ResponseHandler<ClrProfiles>() {
					@Override
					public ClrProfiles handleResponse(
							HttpResponse response)
					throws ClientProtocolException, IOException {
						return parseProfiles(response);
					}
				});

		return profilesMap(response);
	}

	private ClrProfiles parseProfiles(
			HttpResponse response)
	throws ClientProtocolException, IOException {

		final Parameters parameters = parseResponse(response);
		final ClrProfiles profiles = new ClrProfiles(getManager());

		profiles.read(parameters);

		return profiles;
	}

	private Map<ClrProfileId, HttpRfProfile<?>> profilesMap(
			ClrProfiles profiles) {

		final HashMap<ClrProfileId, HttpRfProfile<?>> profilesMap =
				new HashMap<>(profiles.profiles().size());

		for (Map.Entry<ClrProfileId, ClrProfileSettings<?>> e
				: profiles.profiles().entrySet()) {

			final ClrProfileId profileId = e.getKey();
			final ClrProfileSettings<?> settings = e.getValue();

			profilesMap.put(profileId, profile(profileId, settings));
		}

		return profilesMap;
	}

	private <S extends RfSettings> HttpRfProfileImpl<S> profile(
			ClrProfileId profileId,
			ClrProfileSettings<S> settings) {
		return new HttpRfProfileImpl<>(
				this,
				settings.getProvider(),
				profileId,
				settings);
	}

	static Parameters parseResponse(
			HttpResponse response)
	throws ClientProtocolException, IOException {
		checkResponse(response);

		final HttpEntity entity = response.getEntity();
		final Header contentType = entity.getContentType();

		if (!"application/x-www-form-urlencoded".equals(
				getContentMimeType(entity))) {
			throw new InvalidHttpRfResponseException(
					"Wrong response type: " + contentType);
		}

		final String charset = getContentCharSet(entity);
		final Parameters parameters = new Parameters();

		try (InputStreamReader in =
				new InputStreamReader(
						entity.getContent(),
						charset != null ? charset : "UTF-8")) {
			parameters.urlDecode(in);
		}

		return parameters;
	}

	static void checkResponse(
			HttpResponse response)
	throws HttpResponseException {

		final StatusLine statusLine = response.getStatusLine();

		if (statusLine.getStatusCode() < 300) {
			return;
		}

		final Header[] errorCodeHdr = response.getHeaders("X-Error-Code");

		if (errorCodeHdr.length == 0) {
			throw new HttpResponseException(
					statusLine.getStatusCode(),
					statusLine.getReasonPhrase());
		}

		final Header[] errorArgHdrs = response.getHeaders("X-Error-Arg");
		final String[] errorArgs = new String[errorArgHdrs.length];

		for (int i = 0; i < errorArgHdrs.length; ++i) {
			errorArgs[i] = headerValue(errorArgHdrs[i]);
		}

		throw new HttpRfServerException(
				statusLine.getReasonPhrase(),
				statusLine.getStatusCode(),
				headerValue(errorCodeHdr[0]),
				errorArgs);
	}

	static String headerValue(Header header) {
		if (header == null) {
			return null;
		}

		final HeaderElement values[] = header.getElements();

		if (values.length == 0) {
			return null;
		}

		return values[0].getName();
	}

}
