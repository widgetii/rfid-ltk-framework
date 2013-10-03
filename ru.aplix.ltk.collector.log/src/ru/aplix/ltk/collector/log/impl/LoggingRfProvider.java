package ru.aplix.ltk.collector.log.impl;

import static ru.aplix.ltk.collector.log.RfLogConstants.RF_LOG_PROVIDER_ID_SUFFIX;
import static ru.aplix.ltk.collector.log.RfLogConstants.RF_LOG_PROXY_ID;
import static ru.aplix.ltk.core.util.CollectionUtil.toSet;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.osgi.Logger;


class LoggingRfProvider<S extends RfSettings>
		implements RfProvider<S>, Closeable {

	public static <S extends RfSettings>
	LoggingRfProvider<S> loggingRfProvider(
			RfProvider<S> provider,
			TagLog log,
			Logger logger) {
		return new LoggingRfProvider<>(provider, log, logger);
	}

	private final RfProvider<S> provider;
	private final TagLog log;
	private final Logger logger;
	private ServiceRegistration<RfProvider<?>> registration;

	private LoggingRfProvider(
			RfProvider<S> provider,
			TagLog log,
			Logger logger) {
		this.provider = provider;
		this.log = log;
		this.logger = logger;
	}

	public void register(
			BundleContext context,
			ServiceReference<RfProvider<?>> reference) {

		@SuppressWarnings("unchecked")
		final Set<String> oldProxy =
				(Set<String>) toSet(reference.getProperty(RF_PROVIDER_PROXY));
		final Set<String> newProxy =
				new HashSet<>(oldProxy.size() + 1);

		newProxy.addAll(oldProxy);
		newProxy.add(RF_LOG_PROXY_ID);

		final Hashtable<String, Object> properties = new Hashtable<>(2);

		properties.put(RF_PROVIDER_ID, getId());
		properties.put(RF_PROVIDER_PROXY, newProxy);

		this.registration = context.registerService(
				RF_PROVIDER_CLASS,
				this,
				properties);
	}

	public final RfProvider<S> getProvider() {
		return this.provider;
	}

	@Override
	public String getId() {
		return this.provider.getId() + RF_LOG_PROVIDER_ID_SUFFIX;
	}

	@Override
	public String getName() {
		return "Logging " + this.provider.getName();
	}

	@Override
	public Class<? extends S> getSettingsType() {
		return this.provider.getSettingsType();
	}

	@Override
	public S newSettings() {
		return this.provider.newSettings();
	}

	@Override
	public S copySettings(S settings) {
		return this.provider.copySettings(settings);
	}

	@Override
	public RfConnection connect(S settings) {

		final RfConnection connection = this.provider.connect(settings);

		return new LoggingRfConnection(
				settings,
				connection,
				this.log,
				this.logger);
	}

	@Override
	public void close() throws IOException {
		try {
			this.registration.unregister();
		} finally {
			this.log.close();
		}
	}

	@Override
	public String toString() {
		if (this.provider == null) {
			return super.toString();
		}
		return getName() + " (" + getId() + ')';
	}

}
