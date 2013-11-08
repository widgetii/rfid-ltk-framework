package ru.aplix.ltk.collector.http.server;

import static ru.aplix.ltk.collector.http.server.ClrClientServlet.CLR_SERVLET_PATH;
import static ru.aplix.ltk.collector.http.server.ClrProfilesServlet.PROFILES_SERVLET_PATH;

import javax.servlet.ServletException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.osgi.Logger;


public class CollectorHttpService implements BundleActivator {

	private final AllClrProfiles allProfiles;
	private Logger log;
	private HttpTracker httpTracker;
	private BundleContext context;

	public CollectorHttpService() {
		this.allProfiles = new AllClrProfiles(this);
	}

	public final AllClrProfiles allProfiles() {
		return this.allProfiles;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		this.log = new Logger(context);
		this.log.open();
		this.allProfiles.init();
		this.httpTracker = new HttpTracker(context);
		this.httpTracker.open();
	}

	public final BundleContext getContext() {
		return this.context;
	}

	public final Logger log() {
		return this.log;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {

			final HttpService httpService = this.httpTracker.getService();

			if (httpService != null) {
				httpService.unregister(CLR_SERVLET_PATH);
				httpService.unregister(PROFILES_SERVLET_PATH);
			}
		} finally {
			try {
				this.allProfiles.destroy();
			} finally {
				try {
					this.httpTracker.close();
				} finally {
					this.httpTracker = null;
					try {
						this.log.close();
					} finally {
						this.log = null;
					}
				}
			}
		}
	}

	private void registerServlet(HttpService service) {
		try {
			service.registerServlet(
					CLR_SERVLET_PATH,
					new ClrClientServlet(allProfiles()),
					null,
					null);
			service.registerServlet(
					PROFILES_SERVLET_PATH,
					new ClrProfilesServlet(allProfiles()),
					null,
					null);
		} catch (ServletException | NamespaceException e) {
			log().error(
					"Failed to register collector connection servlet",
					e);
		}
	}

	private final class HttpTracker
			extends ServiceTracker<HttpService, HttpService> {

		HttpTracker(BundleContext context) {
			super(context, HttpService.class, null);
		}

		@Override
		public HttpService addingService(
				ServiceReference<HttpService> reference) {

			final HttpService service = super.addingService(reference);

			registerServlet(service);

			return service;
		}

	}

}
