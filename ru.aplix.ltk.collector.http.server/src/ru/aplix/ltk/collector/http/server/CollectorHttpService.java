package ru.aplix.ltk.collector.http.server;

import static org.osgi.framework.Constants.SERVICE_ID;
import static ru.aplix.ltk.collector.http.CollectorHttpConstants.COLLECTOR_HTTP_SERVICE_ID;

import javax.servlet.ServletException;

import org.osgi.framework.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.osgi.Logger;


public class CollectorHttpService implements BundleActivator {

	private Logger log;
	private HttpTracker httpTracker;
	private BundleContext context;

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		this.log = new Logger(context);
		this.log.open();
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

	private void registerServlets(HttpService service) {
		try {
			service.registerServlet(
					"/client",
					new ClrClientServlet(this),
					null,
					null);
		} catch (ServletException | NamespaceException e) {
			log().error(
					"Failed to register collector connection servlet",
					e);
		}
	}

	private static Filter httpServiceFilter(
			BundleContext context)
	throws InvalidSyntaxException {
		return context.createFilter(
				'(' + SERVICE_ID + '=' + COLLECTOR_HTTP_SERVICE_ID + ')');
	}

	private final class HttpTracker
			extends ServiceTracker<HttpService, HttpService> {

		HttpTracker(BundleContext context) throws InvalidSyntaxException {
			super(
					context,
					httpServiceFilter(context),
					null);
		}

		@Override
		public HttpService addingService(
				ServiceReference<HttpService> reference) {

			final HttpService service = super.addingService(reference);

			registerServlets(service);

			return service;
		}

	}

}
