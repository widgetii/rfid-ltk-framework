package ru.aplix.ltk.osgi;

import org.osgi.framework.BundleContext;

import ru.aplix.ltk.core.util.Parameters;


/**
 * Various OSGi utilities.
 */
public final class OSGiUtils {

	/**
	 * Creates unmodifiable parameters with values stored in bundle context.
	 *
	 * <p>The parameters instance created can not be iterated over, and thus
	 * does not support serialization.</p>
	 *
	 * @param context bundle context to get values from
	 *
	 * @return new parameters instance.
	 */
	public static Parameters bundleParameters(BundleContext context) {
		return new Parameters(new BundleContextParameters(context));
	}

	private OSGiUtils() {
	}

}
