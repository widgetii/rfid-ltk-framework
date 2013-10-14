package ru.aplix.ltk.monitor.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class MonitorActivator implements BundleActivator {

	private final MonitorImpl monitor = new MonitorImpl();

	@Override
	public void start(BundleContext context) throws Exception {
		this.monitor.register(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.monitor.unregister();
	}

}
