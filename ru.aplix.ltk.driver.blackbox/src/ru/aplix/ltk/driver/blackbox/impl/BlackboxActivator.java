package ru.aplix.ltk.driver.blackbox.impl;

import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;
import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_ID;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.core.RfProvider;


public class BlackboxActivator implements BundleActivator {

	private ServiceRegistration<RfProvider<?>> providerRegistration;

	@Override
	public void start(BundleContext context) throws Exception {

		final BlackboxRfProvider provider = new BlackboxRfProvider();
		final Hashtable<String, String> properties = new Hashtable<>(1);

		properties.put(RF_PROVIDER_ID, provider.getId());

		this.providerRegistration = context.registerService(
				RF_PROVIDER_CLASS,
				provider,
				properties);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		try {
			this.providerRegistration.unregister();
		} finally {
			this.providerRegistration = null;
		}
	}

}
