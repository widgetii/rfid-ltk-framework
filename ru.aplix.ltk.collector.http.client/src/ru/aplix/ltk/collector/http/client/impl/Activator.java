package ru.aplix.ltk.collector.http.client.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ru.aplix.ltk.collector.http.client.impl.manager.HttpRfManagerImpl;


public class Activator implements BundleActivator {

	private HttpRfProvider provider;
	private HttpRfManagerImpl manager;

	@Override
	public void start(BundleContext context) throws Exception {
		this.provider = new HttpRfProvider();
		this.manager = new HttpRfManagerImpl();
		this.provider.register(context);
		this.manager.register(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			this.provider.unregister();
		} finally {
			this.provider = null;
			this.manager.unregister();
			this.manager = null;
		}
	}

}
