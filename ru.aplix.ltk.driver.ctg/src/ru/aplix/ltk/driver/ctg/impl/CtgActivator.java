package ru.aplix.ltk.driver.ctg.impl;

import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;
import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_ID;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class CtgActivator implements BundleActivator {

	private ServiceRegistration<?> providerRegistration;

	@Override
	public void start(BundleContext context) throws Exception {

		final CtgRfProvider provider = new CtgRfProvider();
		final Hashtable<String, String> properties = new Hashtable<>(1);

		properties.put(RF_PROVIDER_ID, provider.getId());

		this.providerRegistration = context.registerService(
				RF_PROVIDER_CLASS,
				provider,
				properties);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.providerRegistration.unregister();
		this.providerRegistration = null;
	}

}
