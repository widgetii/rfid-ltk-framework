package ru.aplix.ltk.collector.log.impl;

import static ru.aplix.ltk.collector.log.RfLogConstants.RF_LOG_FILTER;
import static ru.aplix.ltk.collector.log.RfLogConstants.RF_LOG_PREFIX;
import static ru.aplix.ltk.collector.log.RfLogConstants.RF_LOG_PROXY_ID;
import static ru.aplix.ltk.collector.log.impl.LoggingRfProvider.loggingRfProvider;
import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_PROXY;
import static ru.aplix.ltk.osgi.OSGiUtils.bundleParameters;

import java.io.IOException;

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

	private LoggingRfProvider<?> createLogingProvider(
			ServiceReference<RfProvider<?>> reference) {

		final RfProvider<?> provider = this.context.getService(reference);
		final TagLog log = createLog(provider);

		if (log == null) {
			this.context.ungetService(reference);
			return null;
		}

		try {

			final LoggingRfProvider<?> loggingProvider =
					loggingRfProvider(provider, log);

			loggingProvider.register(this.context, reference);

			return loggingProvider;
		} catch (Throwable e) {
			this.log.error(
					"Failed to register RFID logging provider for "
					+ provider.getName()
					+ " (" + provider.getId() + ')',
					e);
			try {
				log.close();
			} catch (IOException ex) {
				this.log.error("Error closing log", ex);
			} finally {
				this.context.ungetService(reference);
			}
		}

		return null;
	}

	private TagLog createLog(RfProvider<?> provider) {
		try {
			return new TagLog(this.context, provider);
		} catch (Throwable e) {
			this.log.error(
					"Failed to create a log for " + provider.getName()
					+ " (" + provider.getId() + ')');
		}
		return null;
	}

	private static Filter providersFilter(
			BundleContext context)
	throws InvalidSyntaxException {

		final Parameters params = bundleParameters(context).sub(RF_LOG_PREFIX);
		final String configFilter = params.valueOf(RF_LOG_FILTER);
		final String preventRecursion =
				"(!("+ RF_PROVIDER_PROXY + "=" + RF_LOG_PROXY_ID + "))";
		final String filter;

		if (configFilter == null) {
			filter = preventRecursion;
		} else {
			filter = "(&" + configFilter + preventRecursion + ")";
		}

		return context.createFilter(filter);
	}

	private final class ProviderTracker
			extends ServiceTracker<RfProvider<?>, LoggingRfProvider<?>> {

		ProviderTracker(BundleContext context) throws InvalidSyntaxException {
			super(context, providersFilter(context), null);
		}

		@Override
		public LoggingRfProvider<?> addingService(
				ServiceReference<RfProvider<?>> reference) {
			return createLogingProvider(reference);
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
