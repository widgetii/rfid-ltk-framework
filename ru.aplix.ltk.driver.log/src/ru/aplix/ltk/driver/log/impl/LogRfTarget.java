package ru.aplix.ltk.driver.log.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

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


class LogRfTarget<S extends RfSettings> implements Closeable {

	private final LoggingRfProvider<S> provider;
	private final TagLog log;
	private final String targetId;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private LogRfTracker firstTracker;
	private LogRfTracker lastTracker;
	private RfCollectorHandle handle;

	LogRfTarget(LoggingRfProvider<S> provider, String targetId) {
		this.provider = provider;
		this.targetId = targetId;
		this.log = createLog();
	}

	public final LoggingRfProvider<S> getProvider() {
		return this.provider;
	}

	public final String getTargetId() {
		return this.targetId;
	}

	public final TagLog log() {
		return this.log;
	}

	public final Logger logger() {
		return getProvider().logger();
	}

	public RfConnection connect(S settings) {

		final RfConnection connection =
				getProvider().getProvider().connect(settings);

		return new LoggingRfConnection(settings, createTracker(connection));
	}

	@Override
	public void close() {
		try {
			try {
				if (this.handle != null) {
					try {
						this.handle.unsubscribe();
					} finally {
						this.handle = null;
					}
				}
			} finally {
				this.log.close();
			}
		} catch (Throwable e) {
			logger().error("Error closing log target " + this, e);
		}
	}

	@Override
	public String toString() {
		if (this.provider == null) {
			return super.toString();
		}

		final StringBuilder out = new StringBuilder();

		if (this.targetId != null && !this.targetId.isEmpty()) {
			out.append(this.targetId).append('@');
		}

		final RfProvider<S> provider = getProvider().getProvider();

		out.append(provider.getName())
		.append('(').append(provider.getId()).append(')');

		return out.toString();
	}

	private TagLog createLog() {
		try {
			return new TagLog(getProvider(), getTargetId());
		} catch (IOException e) {
			logger().error(
					"Failed to create log for " + this,
					e);
			throw new IllegalStateException(
					"Failed to create log for " + this,
					e);
		}
	}

	private LogRfTracker createTracker(RfConnection connection) {

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
		logger().error(message, cause);
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
