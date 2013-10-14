package ru.aplix.ltk.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

import ru.aplix.ltk.osgi.Logger;


/**
 * A base class of all types of monitoring.
 *
 * <p>A monitoring instance interface is specific to each monitoring type.</p>
 *
 * <p>Monitoring instance of particular {@link MonitoringTarget target} can be
 * {@link Monitor#monitoringOf(MonitoringTarget) obtained} from the
 * {@link Monitor} service. This instance can be used by the client code to
 * report monitoring events</p>
 *
 * <p>Monitoring instances are thread-safe.</p>
 */
public abstract class Monitoring {

	private final AtomicBoolean stopped = new AtomicBoolean();
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
	 * Logger.
	 *
	 * @return logger to log events to.
	 */
	public final Logger getLogger() {
		return getContext().getLogger();
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
	 * Stops monitoring.
	 *
	 * <p>Does nothing if monitoring is already stopped.</p>
	 */
	public final void stop() {
		if (!this.stopped.compareAndSet(false, true)) {
			return;
		}
		getContext().stopMonitoring(this);
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


}
