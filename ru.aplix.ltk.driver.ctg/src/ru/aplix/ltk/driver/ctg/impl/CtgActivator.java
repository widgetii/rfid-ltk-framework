package ru.aplix.ltk.driver.ctg.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.driver.ctg.CtgRfProvider;


public class CtgActivator implements BundleActivator {

	private ServiceRegistration<?> providerRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		this.providerRegistration = context.registerService(
				new String[] {
					RfProvider.class.getName(),
					CtgRfProvider.class.getName(),
				},
				new CtgRfProviderImpl(),
				null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.providerRegistration.unregister();
		this.providerRegistration = null;
	}

}
