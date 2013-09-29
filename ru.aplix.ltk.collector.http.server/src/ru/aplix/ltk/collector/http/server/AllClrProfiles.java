package ru.aplix.ltk.collector.http.server;

import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;
import static ru.aplix.ltk.core.util.ParameterType.BOOLEAN_PARAMETER_TYPE;
import static ru.aplix.ltk.osgi.OSGiUtils.bundleParameters;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameter;
import ru.aplix.ltk.core.util.Parameters;
import ru.aplix.ltk.osgi.Logger;


public final class AllClrProfiles {

	private static final String CONFIG_PREFIX = "ru.aplix.ltk.rf";
	private static final Parameter<Boolean> AUTOSTART =
			BOOLEAN_PARAMETER_TYPE.parameter("autostart").byDefault(false);

	private final CollectorHttpService collectorService;
	private final ConcurrentHashMap<ClrProfileId, ClrProfile<?>> profiles =
			new ConcurrentHashMap<>();
	private RfProviders rfProviders;
	private HttpClient httpClient;

	public AllClrProfiles(CollectorHttpService collectorService) {
		this.collectorService = collectorService;
	}

	public final CollectorHttpService getCollectorService() {
		return this.collectorService;
	}

	public final Logger log() {
		return getCollectorService().log();
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

	public void statusReport(PrintWriter out, String rootPath) {
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Накопитель тегов RFID</title>");
		out.println("</head>");

		out.println("<body>");
		out.println("<h1>Профили оборудования</h1>");

		RfProvider<?> lastProvider = null;

		for (ClrProfile<?> profile : sortedProfiles()) {

			final RfProvider<?> provider = profile.getProvider();

			if (provider != lastProvider) {
				if (lastProvider != null) {
					out.println("<hr/>");
				}
				lastProvider = provider;

				out.append("<h2>")
				.append(html(profile.getProvider().getName()))
				.append("</h2>")
				.println();
			}

			profileReport(out, rootPath, profile);
		}

		out.println("</body>");
		out.println("</html>");
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

		final ClrProfile<S> profile =
				new ClrProfile<>(this, provider, profileId, params);
		final ClrProfile<?> existing = this.profiles.put(profileId, profile);

		if (existing != null) {
			this.profiles.put(profileId, existing);
			log().error("Profile `" + profileId + "` already registered");
			return;
		}

		if (params.valueOf(AUTOSTART)) {
			profile.autostart();
		}
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

	private Collection<ClrProfile<?>> sortedProfiles() {
		return new TreeMap<>(this.profiles).values();
	}

	private void profileReport(
			PrintWriter out,
			String rootPath,
			ClrProfile<?> profile) {

		final String profileId = profile.getProfileId().toString();
		final String path = attr(rootPath + '/' + profileId);

		out.append("<a href=\"")
		.append(path)
		.append("\">")
		.append(path)
		.append("</a>")
		.println();
	}

	private static String attr(String text) {
		return html(text).replace("\"", "&#34;").replace("'", "&#39;");
	}

	private static String html(String text) {
		return text.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("&", "&amp;");
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
