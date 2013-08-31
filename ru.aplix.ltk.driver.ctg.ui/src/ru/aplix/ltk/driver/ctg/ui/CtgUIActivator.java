package ru.aplix.ltk.driver.ctg.ui;

import static ru.aplix.ltk.ui.RfProviderUI.RF_PROVIDER_UI_CLASS;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.ui.RfProviderUI;

public class CtgUIActivator implements BundleActivator {

	private ServiceRegistration<RfProviderUI<?>> uiRegistration;

	@Override
	public void start(BundleContext context) {
		this.uiRegistration = context.registerService(
				RF_PROVIDER_UI_CLASS,
				new CtgRfProviderUI(),
				null);
	}

	@Override
	public void stop(BundleContext context) {
		this.uiRegistration.unregister();
		this.uiRegistration = null;
	}

}
