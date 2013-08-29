package ru.aplix.ltk.driver.dummy.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.driver.dummy.DummyRfProvider;


public class DummyActivator implements BundleActivator {

	private ServiceRegistration<?> providerReg;

	@Override
	public void start(BundleContext context) throws Exception {
		this.providerReg = context.registerService(
				new String[] {
						RfProvider.class.getName(),
						DummyRfProvider.class.getName(),
				},
				new DummyRfProvider(),
				null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.providerReg.unregister();
	}

}
