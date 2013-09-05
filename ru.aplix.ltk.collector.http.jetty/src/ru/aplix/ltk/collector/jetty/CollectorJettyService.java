package ru.aplix.ltk.collector.jetty;

import static org.eclipse.equinox.http.jetty.JettyConfigurator.startServer;
import static org.eclipse.equinox.http.jetty.JettyConfigurator.stopServer;
import static org.osgi.framework.Constants.SERVICE_ID;

import java.util.Collection;
import java.util.Hashtable;

import org.eclipse.equinox.http.jetty.JettyConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;

import ru.aplix.ltk.collector.http.CollectorHttpService;


public class CollectorJettyService implements CollectorHttpService {

	private ServiceRegistration<CollectorHttpService> jettyRegistration;
	private HttpService httpService;

	@Override
	public HttpService getHttpService() {
		return this.httpService;
	}

	final void register(BundleContext context) throws Exception {
		this.httpService = registerHttpService(context);
		this.jettyRegistration = context.registerService(
				CollectorHttpService.class,
				this,
				null);
	}

	final void unregister() throws Exception {
		this.httpService = null;
		try {
			this.jettyRegistration.unregister();
		} finally {
			this.jettyRegistration = null;
			stopServer(COLLECTOR_HTTP_SERVICE_ID);
		}
	}

	private HttpService registerHttpService(
			BundleContext context)
	throws Exception {

		final Hashtable<String, Object> settings = new Hashtable<>();

		settings.put(JettyConstants.CONTEXT_PATH, "/");
		startServer(COLLECTOR_HTTP_SERVICE_ID, settings);

		final Collection<ServiceReference<HttpService>> refs =
				context.getServiceReferences(
						HttpService.class,
						"(" + SERVICE_ID
						+ "=" + COLLECTOR_HTTP_SERVICE_ID + ")");

		if (refs.isEmpty()) {
			throw new IllegalStateException(
					"Collector HTTP service not registered");
		}

		return context.getService(refs.iterator().next());
	}

}
