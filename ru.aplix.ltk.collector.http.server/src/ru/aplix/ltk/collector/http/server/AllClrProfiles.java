package ru.aplix.ltk.collector.http.server;

import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;

import java.io.File;
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
import ru.aplix.ltk.osgi.Logger;


public final class AllClrProfiles {

	private final CollectorHttpService collectorService;
	private final ConcurrentHashMap<String, ProviderClrProfiles<?>> profiles =
			new ConcurrentHashMap<>();
	private File configDir;
	private RfProviders rfProviders;
	private HttpClient httpClient;

	public AllClrProfiles(CollectorHttpService collectorService) {
		this.collectorService = collectorService;
	}

	public final CollectorHttpService getCollectorService() {
		return this.collectorService;
	}

	public final File getConfigDir() {
		return this.configDir;
	}

	public final Logger log() {
		return getCollectorService().log();
	}

	public void init() {
		this.configDir = configDir();
		this.rfProviders = new RfProviders();
		this.rfProviders.open();
	}

	public final ClrProfile<?> get(ClrProfileId profileId) {

		final ProviderClrProfiles<?> providerProfiles =
				this.profiles.get(profileId.getProviderId());

		if (providerProfiles == null) {
			return null;
		}

		return providerProfiles.get(profileId.getId());
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

	private File configDir() {

		final String configDirPath =
				getCollectorService()
				.getContext()
				.getProperty("ru.aplix.ltk.rf.configDir");
		final File configDir;

		if (configDirPath != null) {
			configDir = new File(configDirPath);
		} else {
			configDir =
					getCollectorService().getContext().getDataFile("profiles");
		}
		if (!configDir.isDirectory() && !this.configDir.mkdir()) {
			throw new IllegalStateException(
					"Can not create collector configuration directory: "
					+ configDir);
		}

		return configDir;
	}

	private <S extends RfSettings> void addProvider(RfProvider<S> provider) {
		log().debug("Add provider: " + provider.getId());

		final String providerId = provider.getId();
		final ProviderClrProfiles<S> providerProfiles =
				new ProviderClrProfiles<>(this, provider);

		this.profiles.put(providerId, providerProfiles);
		providerProfiles.load();
	}

	private void removeProvider(RfProvider<?> provider) {

		final ProviderClrProfiles<?> providerProfiles =
				this.profiles.remove(provider.getId());

		if (providerProfiles != null) {
			providerProfiles.dispose();
		}
	}

	private Collection<ClrProfile<?>> sortedProfiles() {

		final TreeMap<ClrProfileId, ClrProfile<?>> profiles = new TreeMap<>();

		for (ProviderClrProfiles<?> providerProfiles : this.profiles.values()) {
			for (ClrProfile<?> profile : providerProfiles) {
				profiles.put(profile.getProfileId(), profile);
			}
		}

		return profiles.values();
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
