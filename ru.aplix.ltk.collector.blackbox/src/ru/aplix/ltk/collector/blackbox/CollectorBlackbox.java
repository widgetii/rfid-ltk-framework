package ru.aplix.ltk.collector.blackbox;

import static ru.aplix.ltk.collector.blackbox.BlackboxServlet.BLACKBOX_SERVLET_PATH;

import javax.servlet.ServletException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.osgi.Logger;


public class CollectorBlackbox implements BundleActivator {

	private BundleContext context;
	private Logger log;
	private HttpTracker httpTracker;

	public final BundleContext getContext() {
		return this.context;
	}

	public final Logger log() {
		return this.log;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		this.log = new Logger(context);
		this.log.open();
		this.httpTracker = new HttpTracker(context);
		this.httpTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {

			final HttpService httpService = this.httpTracker.getService();

			if (httpService != null) {
				httpService.unregister(BLACKBOX_SERVLET_PATH);
			}
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

	private void registerServlet(HttpService service) {
		try {
			service.registerServlet(
					BLACKBOX_SERVLET_PATH,
					new BlackboxServlet(this),
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
