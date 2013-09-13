package ru.aplix.ltk.collector.http.client.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

	private HttpRfProvider provider;

	@Override
	public void start(BundleContext context) throws Exception {
		this.provider = new HttpRfProvider();
		this.provider.register(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			this.provider.unregister();
		} finally {
			this.provider = null;
		}
	}

}
