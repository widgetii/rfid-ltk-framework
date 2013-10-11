package ru.aplix.ltk.driver.log.impl;

import static ru.aplix.ltk.core.util.CollectionUtil.toSet;
import static ru.aplix.ltk.driver.log.RfLogConstants.RF_LOG_PROVIDER_ID_SUFFIX;
import static ru.aplix.ltk.driver.log.RfLogConstants.RF_LOG_PROXY_ID;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.osgi.Logger;


class LoggingRfProvider<S extends RfSettings>
		implements RfProvider<S>, Closeable {

	private final BundleContext context;
	private final RfProvider<S> provider;
	private final Logger logger;
	private final HashMap<String, LogRfTarget<S>> targets = new HashMap<>();
	private ServiceRegistration<RfProvider<?>> registration;

	LoggingRfProvider(
			BundleContext context,
			RfProvider<S> provider,
			Logger logger) {
		this.context = context;
		this.provider = provider;
		this.logger = logger;
	}

	public final BundleContext getContext() {
		return this.context;
	}

	public final RfProvider<S> getProvider() {
		return this.provider;
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

	public final Logger logger() {
		return this.logger;
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
		return target(settings.getTargetId()).connect(settings);
	}

	@Override
	public void close() throws IOException {
		synchronized (this.targets) {
			for (LogRfTarget<S> target : this.targets.values()) {
				target.close();
			}
			this.targets.clear();
		}
		this.registration.unregister();
	}

	@Override
	public String toString() {
		if (this.provider == null) {
			return super.toString();
		}
		return getName() + " (" + getId() + ')';
	}

	private LogRfTarget<S> target(String targetId) {
		synchronized (this.targets) {

			final LogRfTarget<S> found = this.targets.get(targetId);

			if (found != null) {
				return found;
			}

			final LogRfTarget<S> target = new LogRfTarget<>(this, targetId);

			this.targets.put(targetId, target);

			return target;
		}
	}
}
