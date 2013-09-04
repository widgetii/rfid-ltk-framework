package ru.aplix.ltk.driver.dummy.impl;

import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class DummyActivator implements BundleActivator {

	private ServiceRegistration<?> providerReg;

	@Override
	public void start(BundleContext context) throws Exception {
		this.providerReg = context.registerService(
				RF_PROVIDER_CLASS,
				new DummyRfProvider(),
				null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.providerReg.unregister();
	}

}
