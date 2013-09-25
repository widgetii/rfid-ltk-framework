package ru.aplix.ltk.collector.log.impl;

import static ru.aplix.ltk.collector.log.RfLogConstants.RF_LOG_PROVIDER_ID_SUFFIX;
import static ru.aplix.ltk.collector.log.impl.LoggingRfProvider.loggingRfProvider;
import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_ID;
import static ru.aplix.ltk.core.RfProvider.RF_PROXIED_PROVIDERS;

import java.io.IOException;
import java.util.Set;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.core.RfProvider;
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

		final String providerId =
				(String) reference.getProperty(RF_PROVIDER_ID);

		if (providerId == null) {
			return null;
		}

		final String logId = providerId + RF_LOG_PROVIDER_ID_SUFFIX;
		@SuppressWarnings("unchecked")
		final Set<String> proxied =
				(Set<String>) reference.getProperty(RF_PROXIED_PROVIDERS);

		if (proxied != null && proxied.contains(logId)) {
			return null;
		}

		final RfProvider<?> provider = this.context.getService(reference);

		if (providerId.endsWith(RF_LOG_PROVIDER_ID_SUFFIX)) {
			return null;
		}

		final TagLog log = createLog(providerId, provider);

		if (log == null) {
			this.context.ungetService(reference);
			return null;
		}

		try {

			final LoggingRfProvider<?> loggingProvider =
					loggingRfProvider(provider, log);

			loggingProvider.register(this.context, providerId, proxied);

			return loggingProvider;
		} catch (Throwable e) {
			this.log.error(
					"Failed to register RFID logging provider for "
							+ provider.getName() + " (" + providerId + ')',
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

	private TagLog createLog(
			final String providerId,
			final RfProvider<?> provider) {
		try {
			return new TagLog(this.context, provider);
		} catch (Throwable e) {
			this.log.error(
					"Failed to create a log for " + provider.getName()
					+ " (" + providerId + ')');
		}
		return null;
	}

	private final class ProviderTracker
			extends ServiceTracker<RfProvider<?>, LoggingRfProvider<?>> {

		ProviderTracker(BundleContext context) {
			super(context, RfProvider.RF_PROVIDER_CLASS, null);
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
