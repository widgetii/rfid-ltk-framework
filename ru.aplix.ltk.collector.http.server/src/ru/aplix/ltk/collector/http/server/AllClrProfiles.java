package ru.aplix.ltk.collector.http.server;

import static java.util.Collections.synchronizedMap;
import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;
import static ru.aplix.ltk.osgi.OSGiUtils.bundleParameters;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameters;


public final class AllClrProfiles {

	private static final String CONFIG_PREFIX = "ru.aplix.ltk.rf";

	private final CollectorHttpService collectorService;
	private final Map<ClrProfileId, ClrProfile<?>> profiles =
			synchronizedMap(new HashMap<ClrProfileId, ClrProfile<?>>());
	private RfProviders rfProviders;
	private HttpClient httpClient;

	public AllClrProfiles(CollectorHttpService collectorService) {
		this.collectorService = collectorService;
	}

	public final CollectorHttpService getCollectorService() {
		return this.collectorService;
	}

	public void init() {
		this.rfProviders = new RfProviders();
		this.rfProviders.open();
	}

	public final ClrProfile<?> get(ClrProfileId profileId) {
		return this.profiles.get(profileId);
	}

	public synchronized final HttpClient httpClient() {
		if (this.httpClient != null) {
			return this.httpClient;
		}

		final ThreadSafeClientConnManager conman =
				new ThreadSafeClientConnManager();

		return this.httpClient = new DefaultHttpClient(conman);
	}

	public void destroy() {
		this.rfProviders.close();
		synchronized (this) {
			if (this.httpClient != null) {
				try {
					this.httpClient.getConnectionManager().shutdown();
				} finally {
					this.httpClient = null;
				}
			}
		}
	}

	private void addProvider(RfProvider<?> provider) {

		final Parameters params = providerParameters(provider);
		final String[] configs = params.valuesOf("");

		if (configs == null) {
			addProfile(provider, new ClrProfileId(provider.getId()), params);
		} else {
			for (String config : configs) {
				addProfile(
						provider,
						new ClrProfileId(provider.getId(), config),
						params.sub(config));
			}
		}
	}

	private <S extends RfSettings> void addProfile(
			RfProvider<S> provider,
			ClrProfileId profileId,
			Parameters params) {
		this.profiles.put(
				profileId,
				new ClrProfile<>(this, provider, profileId, params));
	}

	private void removeProvider(RfProvider<?> provider) {

		final Parameters params = providerParameters(provider);
		final String[] configs = params.valuesOf("");

		if (configs == null) {
			removeProfile(new ClrProfileId(provider.getId()));
		} else {
			for (String config : configs) {
				removeProfile(new ClrProfileId(provider.getId(), config));
			}
		}
	}

	private void removeProfile(ClrProfileId profileId) {

		final ClrProfile<?> profile = this.profiles.remove(profileId);

		if (profile != null) {
			profile.dispose();
		}
	}

	private Parameters providerParameters(RfProvider<?> provider) {
		return bundleParameters(getCollectorService().getContext())
				.sub(CONFIG_PREFIX + '.' + provider.getId());
	}

	private final class RfProviders
			extends ServiceTracker<RfProvider<?>, RfProvider<?>> {

		RfProviders() {
			super(
					getCollectorService().getContext(),
					RF_PROVIDER_CLASS,
					null);
		}

		@Override
		public RfProvider<?> addingService(
				ServiceReference<RfProvider<?>> reference) {

			final RfProvider<?> provider = super.addingService(reference);

			addProvider(provider);

			return provider;
		}

		@Override
		public void removedService(
				ServiceReference<RfProvider<?>> reference,
				RfProvider<?> service) {
			removeProvider(service);
			super.removedService(reference, service);
		}

	}

}
