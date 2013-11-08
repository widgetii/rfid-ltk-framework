package ru.aplix.ltk.collector.http.client.impl.manager;

import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.collector.http.client.HttpRfManager;
import ru.aplix.ltk.collector.http.client.HttpRfServer;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfProvidersResolver;


public class HttpRfManagerImpl implements HttpRfManager, RfProvidersResolver {

	private ServiceRegistration<HttpRfManager> registration;
	private final ConcurrentHashMap<String, RfProvider<?>> providers =
			new ConcurrentHashMap<>();
			private RfTracker rfTracker;
	private HttpClient httpClient;

	public void register(BundleContext context) {
		this.registration = context.registerService(
				HttpRfManager.class,
				this,
				null);
		this.rfTracker = new RfTracker(context);
		this.rfTracker.open();
		this.httpClient =
				new DefaultHttpClient(new ThreadSafeClientConnManager());
	}

	public final HttpClient httpClient() {
		return this.httpClient;
	}

	@Override
	public HttpRfServer httpRfServer(URL url) {
		return new HttpRfServerImpl(this, url);
	}

	@Override
	public RfProvider<?> rfProviderById(String providerId) {
		return this.providers.get(providerId);
	}

	public void unregister() {
		try {
			this.registration.unregister();
		} finally {
			this.registration = null;
			try {
				this.rfTracker.close();
			} finally {
				this.rfTracker = null;
				this.httpClient.getConnectionManager().shutdown();
				this.httpClient = null;
			}
		}
	}

	private final class RfTracker
			extends ServiceTracker<RfProvider<?>, RfProvider<?>> {

		RfTracker(BundleContext context) {
			super(context, RF_PROVIDER_CLASS, null);
		}

		@Override
		public RfProvider<?> addingService(
				ServiceReference<RfProvider<?>> reference) {

			final RfProvider<?> provider = super.addingService(reference);

			HttpRfManagerImpl.this.providers.put(provider.getId(), provider);

			return provider;
		}

		@Override
		public void removedService(
				ServiceReference<RfProvider<?>> reference,
				RfProvider<?> service) {
			HttpRfManagerImpl.this.providers.remove(service.getId());
			super.removedService(reference, service);
		}

	}

}
