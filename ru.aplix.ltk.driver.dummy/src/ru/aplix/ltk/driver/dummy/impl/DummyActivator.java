package ru.aplix.ltk.driver.dummy.impl;

import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;
import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_ID;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class DummyActivator implements BundleActivator {

	private ServiceRegistration<?> providerReg;

	@Override
	public void start(BundleContext context) throws Exception {

		final DummyRfProvider provider = new DummyRfProvider();
		final Hashtable<String, String> properties = new Hashtable<>(1);

		properties.put(RF_PROVIDER_ID, provider.getId());

		this.providerReg = context.registerService(
				RF_PROVIDER_CLASS,
				provider,
				properties);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.providerReg.unregister();
	}

}
