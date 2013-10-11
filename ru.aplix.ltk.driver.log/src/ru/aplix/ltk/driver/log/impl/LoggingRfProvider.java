package ru.aplix.ltk.driver.log.impl;

import static ru.aplix.ltk.core.util.CollectionUtil.toSet;
import static ru.aplix.ltk.driver.log.RfLogConstants.RF_LOG_PROVIDER_ID_SUFFIX;
import static ru.aplix.ltk.driver.log.RfLogConstants.RF_LOG_PROXY_ID;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfError;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
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
	private LogRfTracker firstTracker;
	private LogRfTracker lastTracker;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ServiceRegistration<RfProvider<?>> registration;
	private RfCollectorHandle handle;

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

	public final TagLog log() {
		return this.log;
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

		final RfConnection connection = getProvider().connect(settings);

		return new LoggingRfConnection(settings, createTracker(connection));
	}

	@Override
	public void close() throws IOException {
		try {
			if (this.handle != null) {
				this.handle.unsubscribe();
			}
		} finally {
			this.handle = null;
			try {
				this.registration.unregister();
			} finally {
				this.log.close();
			}
		}
	}

	@Override
	public String toString() {
		if (this.provider == null) {
			return super.toString();
		}
		return getName() + " (" + getId() + ')';
	}

	final LogRfTracker createTracker(RfConnection connection) {

		final WriteLock lock = this.lock.writeLock();

		lock.lock();
		try {
			if (this.handle == null) {
				this.handle = connection.getCollector().subscribe(
						new ProviderStatusListener());
			}
		} finally {
			lock.unlock();
		}

		return new LogRfTracker(this);
	}

	final void addTracker(LogRfTracker tracker) {

		final WriteLock lock = this.lock.writeLock();

		lock.lock();
		try {
			if (this.firstTracker == null) {
				this.firstTracker = this.lastTracker = tracker;
				return;
			}
			tracker.setPrev(this.lastTracker);
			this.lastTracker.setNext(tracker);
			this.lastTracker = tracker;
		} finally {
			lock.unlock();
		}
	}

	final void removeTracker(LogRfTracker tracker) {

		final WriteLock lock = this.lock.writeLock();

		lock.lock();
		try {

			final LogRfTracker prev = tracker.getPrev();
			final LogRfTracker next = tracker.getNext();

			if (prev == null) {
				this.firstTracker = next;
			} else {
				prev.setNext(next);
			}
			if (next == null) {
				this.lastTracker = prev;
			} else {
				next.setPrev(prev);
			}
		} finally {
			lock.unlock();
		}
	}

	private void logTag(RfTagAppearanceMessage message) {

		final RfTagAppearanceMessage logged;

		try {
			logged = this.log.log(message);
		} catch (Throwable e) {
			updateTagAppearance(message);
			error("Failed to log message", e);
			return;
		}

		updateTagAppearance(logged);
	}

	private void updateStatus(RfStatusMessage status) {

		final ReadLock lock = this.lock.readLock();

		lock.lock();
		try {

			LogRfTracker tracker = this.firstTracker;

			while (tracker != null) {
				tracker.updateStatus(status);
				tracker = tracker.getNext();
			}
		} finally {
			lock.unlock();
		}
	}

	private void updateTagAppearance(RfTagAppearanceMessage tagAppearance) {

		final ReadLock lock = this.lock.readLock();

		lock.lock();
		try {

			LogRfTracker tracker = this.firstTracker;

			while (tracker != null) {
				tracker.updateTagAppearance(tagAppearance);
				tracker = tracker.getNext();
			}
		} finally {
			lock.unlock();
		}
	}

	private void error(String message, Throwable cause) {
		this.logger.error(message, cause);
		updateStatus(new RfError(null, message, cause));
	}

	private final class ProviderStatusListener
			implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

		@Override
		public void consumerSubscribed(RfCollectorHandle handle) {
			handle.requestTagAppearance(new ProviderTagListener());
		}

		@Override
		public void messageReceived(RfStatusMessage message) {
			updateStatus(message);
		}

		@Override
		public void consumerUnsubscribed(RfCollectorHandle handle) {
		}

	}

	private final class ProviderTagListener implements MsgConsumer<
			RfTagAppearanceHandle,
			RfTagAppearanceMessage> {

		@Override
		public void consumerSubscribed(RfTagAppearanceHandle handle) {
		}

		@Override
		public void messageReceived(RfTagAppearanceMessage message) {
			logTag(message);
		}

		@Override
		public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
		}

	}

}
