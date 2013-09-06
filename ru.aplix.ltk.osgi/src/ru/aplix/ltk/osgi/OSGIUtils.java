package ru.aplix.ltk.osgi;

import org.osgi.framework.BundleContext;

import ru.aplix.ltk.core.util.Parameters;


public final class OSGIUtils {

	public static Parameters bundleParameters(BundleContext context) {
		return new Parameters(new BundleContextParameters(context));
	}

	private OSGIUtils() {
	}

}
