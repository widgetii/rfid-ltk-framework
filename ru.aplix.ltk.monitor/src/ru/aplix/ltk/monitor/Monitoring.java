package ru.aplix.ltk.monitor;

import static java.util.Objects.requireNonNull;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


/**
 * A base class of all types of monitoring.
 *
 * <p>A monitoring instance interface is specific to each monitoring type.</p>
 *
 * <p>Monitoring instance of particular {@link MonitoringTarget target} can be
 * {@link Monitor#monitoringOf(MonitoringTarget) obtained} from the
 * {@link Monitor} service. This instance can be used by the client code to
 * report monitoring events. It is a monitoring instance responsibility to
 * report these events to the
 * {@link #addListener(MonitoringListener) registered} listeners.</p>
 *
 * <p>Monitoring instances are thread-safe.</p>
 *
 * @param <L> supported monitoring listeners type.
 */
public abstract class Monitoring<L extends MonitoringListener> {

	private final AtomicBoolean stopped = new AtomicBoolean();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final LinkedList<L> listeners = new LinkedList<>();
	private MonitoringTarget<?> target;
	private MonitoringContext context;

	/**
	 * Monitoring context.
	 *
	 * @return a context instance this monitoring is initialized with.
	 */
	public final MonitoringContext getContext() {
		return this.context;
	}

	/**
	 * Monitoring target.
	 *
	 * @return the target of this monitoring instance.
	 */
	public final MonitoringTarget<?> getTarget() {
		return this.target;
	}

	/**
	 * Whether monitoring is stopped.
	 *
	 * @return <code>true</code> if {@link #stop()} called,
	 * or <code>false</code> otherwise.
	 */
	public final boolean isStopped() {
		return this.stopped.get();
	}

	/**
	 * Registers a monitoring listener.
	 *
	 * <p>It is a monitoring implementation's responsibility to
	 * {@link #notifyListeners(Notifier) notify} the registered listeners on
	 * monitoring events.</p>
	 *
	 * @param listener a listener to register.
	 */
	public void addListener(L listener) {
		requireNonNull(listener, "Monitoring listener not specified");
		if (isStopped()) {
			getContext().getLogger().error(
					"Failed to add monitoring listener " + listener
					+ ". Moniitoring of " + getTarget()
					+ " is already stopped");
			return;
		}

		final WriteLock lock = lock().writeLock();

		lock.lock();
		try {
			this.listeners.add(listener);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Stops monitoring.
	 *
	 * <p>{@link #notifyMonitoringStopped(MonitoringListener) Notifies}
	 * listeners.</p>
	 *
	 * <p>Does nothing if monitoring is already stopped.</p>
	 */
	public final void stop() {
		if (!this.stopped.compareAndSet(false, true)) {
			return;
		}
		getContext().stopMonitoring(this);
		notifyMonitoringStopped();
	}

	/**
	 * Builds monitoring report for this target.
	 *
	 * <p>This method updates the {@link MonitoringReport#getTarget() report
	 * target}, invokes {@link #buildReport(MonitoringReport)} method, and
	 * restores the previous report target after that.</p>
	 *
	 * @param reports monitoring reports to build.
	 */
	public final void report(MonitoringReport reports) {

		final MonitoringTarget<?> previousTarget =
				reports.startReporting(getTarget());

		buildReport(reports);

		reports.stopReporting(previousTarget);
	}

	/**
	 * Initialized monitoring instance.
	 *
	 * <p>This method is invoked right after monitoring instance
	 * {@link MonitoringContext#createMonitoring(MonitoringTarget) construction}
	 * </p>
	 */
	protected abstract void init();

	/**
	 * A lock used to add, remove, and notify listeners.</p>
	 *
	 * @return reentrant read/write lock.
	 */
	protected final ReentrantReadWriteLock lock() {
		return this.lock;
	}

	/**
	 * Notifies all registered listeners on particular event.
	 *
	 * @param notifier monitoring events notifier.
	 */
	protected void notifyListeners(Notifier notifier) {
		if (isStopped()) {
			getContext().getLogger().error(
					"Monitoring of " + getTarget()
					+ " is already stopped. Can not notify listeners");
			return;
		}

		final ReadLock lock = lock().readLock();

		lock.lock();
		try {
			for (L listener : this.listeners) {
				try {
					notifier.notifyListener(listener);
				} catch (Throwable e) {
					getContext().getLogger().error(
							"Error notifying monitoring listener " + listener,
							e);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Notifies the given listener that the monitoring is stopped.
	 *
	 * @param listener monitoring listener to notify.
	 */
	protected abstract void notifyMonitoringStopped(L listener);

	/**
	 * Implements the monitoring reporting.
	 *
	 * <p>This method is invoked from {@link #report(MonitoringReport)} one
	 * with proper {@link MonitoringReport#getTarget() report target} set.</p>
	 *
	 * @param report monitoring report to build.
	 */
	protected abstract void buildReport(MonitoringReport report);

	final void initMonitoring(
			MonitoringContext context,
			MonitoringTarget<?> target) {
		this.context = context;
		this.target = target;
		context.startMonitoring(this);
		init();
	}

	private void notifyMonitoringStopped() {

		final WriteLock lock = lock().writeLock();

		lock.lock();
		try {
			for (L listener : this.listeners) {
				try {
					notifyMonitoringStopped(listener);
				} catch (Throwable e) {
					getContext().getLogger().error(
							"Error notifying monitoring listener " + listener,
							e);
				}
			}
			this.listeners.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Monitoring listener notifier.
	 *
	 * <p>It is used by {@link #notifyListener(MonitoringListener)} method to
	 * implement the notification about particular type of event.</p>
	 */
	protected abstract class Notifier {

		/**
		 * Notifies the given listener on particular type of event.
		 *
		 * @param listener monitoring listener to notify.
		 */
		public abstract void notifyListener(L listener);

	}

}
