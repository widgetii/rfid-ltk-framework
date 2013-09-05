package ru.aplix.ltk.collector.jetty;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

	private BundleContext context;
	private CollectorJettyService jettyService;

	public final BundleContext getContext() {
		return this.context;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;

		final CollectorJettyService jettyService = new CollectorJettyService();

		jettyService.register(context);
		this.jettyService = jettyService;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		try {
			this.jettyService.unregister();
		} finally {
			this.jettyService = null;
			this.context = null;
		}
	}

}
