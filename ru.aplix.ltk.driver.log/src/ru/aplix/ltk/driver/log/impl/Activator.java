package ru.aplix.ltk.driver.log.impl;

import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_PROXY;
import static ru.aplix.ltk.driver.log.RfLogConstants.RF_LOG_FILTER;
import static ru.aplix.ltk.driver.log.RfLogConstants.RF_LOG_PREFIX;
import static ru.aplix.ltk.driver.log.RfLogConstants.RF_LOG_PROXY_ID;
import static ru.aplix.ltk.osgi.OSGiUtils.bundleParameters;

import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.util.Parameters;
import ru.aplix.ltk.osgi.Logger;


public class Activator implements BundleActivator {

	private Logger log;
	private ProviderTracker tracker;
	private BundleContext context;

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		this.log = new Logger(context);
		this.log.open();
		this.tracker = new ProviderTracker(context);
		this.tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			this.tracker.close();
		} finally {
			this.tracker = null;
			this.context = null;
			this.log.close();
		}
	}

	private LoggingRfProvider<?> createLoggingProvider(
			ServiceReference<RfProvider<?>> reference) {

		final RfProvider<?> provider = this.context.getService(reference);

		try {

			@SuppressWarnings({"rawtypes", "unchecked"})
			final LoggingRfProvider<?> loggingProvider =
					new LoggingRfProvider(this.context, provider, this.log);

			loggingProvider.register(this.context, reference);

			return loggingProvider;
		} catch (Throwable e) {
			this.log.error(
					"Failed to register RFID logging provider for "
					+ provider.getName()
					+ " (" + provider.getId() + ')',
					e);
			try {
				this.log.close();
			} finally {
				this.context.ungetService(reference);
			}
		}

		return null;
	}

	private static Filter providersFilter(
			BundleContext context)
	throws InvalidSyntaxException {

		final StringBuilder filter = new StringBuilder();

		filter.append("(&");

		filter.append('(')
		.append(Constants.OBJECTCLASS)
		.append('=')
		.append(RfProvider.class.getName())
		.append(')');

		filter.append("(!(")
		.append(RF_PROVIDER_PROXY)
		.append('=')
		.append(RF_LOG_PROXY_ID)
		.append("))");

		final Parameters params = bundleParameters(context).sub(RF_LOG_PREFIX);
		final String configFilter = params.valueOf(RF_LOG_FILTER);

		if (configFilter != null) {
			filter.append(configFilter);
		}

		filter.append(")");

		return context.createFilter(filter.toString());
	}

	private final class ProviderTracker
			extends ServiceTracker<RfProvider<?>, LoggingRfProvider<?>> {

		ProviderTracker(BundleContext context) throws InvalidSyntaxException {
			super(context, providersFilter(context), null);
		}

		@Override
		public LoggingRfProvider<?> addingService(
				ServiceReference<RfProvider<?>> reference) {
			return createLoggingProvider(reference);
		}

		@Override
		public void removedService(
				ServiceReference<RfProvider<?>> reference,
				LoggingRfProvider<?> service) {
			try {
				service.close();
			} catch (Throwable e) {
				Activator.this.log.error(
						"Error while closing " + service,
						e);
			} finally {
				this.context.ungetService(reference);
			}
		}

	}

}
