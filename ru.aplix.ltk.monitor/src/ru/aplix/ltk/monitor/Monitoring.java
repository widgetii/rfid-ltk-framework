package ru.aplix.ltk.monitor;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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
	private final AtomicLong nextEventId = new AtomicLong();
	private final LinkedList<MonitoringEvent> events = new LinkedList<>();

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
	 * @param report monitoring report to build.
	 */
	public final void report(MonitoringReport report) {

		final MonitoringTarget<?> previousTarget =
				report.startReporting(getTarget());

		buildReport(report);
		report.printLines();

		report.endReporting(previousTarget);
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
	protected void buildReport(MonitoringReport report) {
		for (MonitoringEvent event : this.events) {
			event.report(report);
		}
	}

	/**
	 * Creates trace monitoring event.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent trace() {
		return trace(null);
	}

	/**
	 * Creates named trace monitoring event.
	 *
	 * @param name event name.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent trace(String name) {
		return new MonitoringEvent(this, MonitoringSeverity.TRACE, name);
	}

	/**
	 * Creates info monitoring event.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent info() {
		return info(null);
	}

	/**
	 * Creates name info monitoring event.
	 *
	 * @param name event name.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent info(String name) {
		return new MonitoringEvent(this, MonitoringSeverity.INFO, name);
	}

	/**
	 * Creates reminder monitoring event.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent reminder() {
		return reminder(null);
	}

	/**
	 * Creates named reminder monitoring event.
	 *
	 * @param name event name.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent reminder(String name) {
		return new MonitoringEvent(this, MonitoringSeverity.REMINDER, name);
	}

	/**
	 * Creates note monitoring event.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent note() {
		return note(null);
	}

	/**
	 * Creates named note monitoring event.
	 *
	 * @param name event name.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent note(String name) {
		return new MonitoringEvent(this, MonitoringSeverity.NOTE, name);
	}

	/**
	 * Creates warning monitoring event.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent warning() {
		return warning(null);
	}

	/**
	 * Creates named warning monitoring event.
	 *
	 * @param name event name.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent warning(String name) {
		return new MonitoringEvent(this, MonitoringSeverity.WARNING, name);
	}

	/**
	 * Creates error monitoring event.
	 *
	 * <p>After 10 minutes a fatal error will raise. A warning will be
	 * generated when forgotten.</p>
	 *
	 * @return new monitoring event.
	 *
	 * @see #error(String)
	 */
	protected final MonitoringEvent error() {
		return error(null);
	}

	/**
	 * Creates named error monitoring event.
	 *
	 * <p>After 10 minutes a fatal error will be activated instead of this
	 * error. A warning will be raised when forgotten.</p>
	 *
	 * @param name event name.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent error(String name) {

		final MonitoringEvent error =
				new MonitoringEvent(this, MonitoringSeverity.ERROR, name);

		error.afterTimeoutActivate(
				600000L,
				new MonitoringEvent(
						this,
						MonitoringSeverity.FATAL,
						name != null ? name + " (fatal)" : null));
		error.occurWhenForgot(
				new MonitoringEvent(
						this,
						MonitoringSeverity.WARNING,
						name != null ? name + " (warning)" : null));

		return error;
	}

	/**
	 * Creates fatal error monitoring event.
	 *
	 * @return new monitoring event.
	 *
	 * @see #fatal(String)
	 */
	protected final MonitoringEvent fatal() {
		return fatal(null);
	}

	/**
	 * Creates named fatal error monitoring event.
	 *
	 * <p>A warning will be raised when forgotten.</p>
	 *
	 * @param name event name.
	 *
	 * @return new monitoring event.
	 */
	protected final MonitoringEvent fatal(String name) {

		final MonitoringEvent fatal =
				new MonitoringEvent(this, MonitoringSeverity.FATAL, name);

		fatal.occurWhenForgot(
				new MonitoringEvent(
						this,
						MonitoringSeverity.WARNING,
						name != null ? name + " (warning)" : null));

		return fatal;
	}

	final void initMonitoring(
			MonitoringContext context,
			MonitoringTarget<?> target) {
		this.context = context;
		this.target = target;
		context.startMonitoring(this);
		init();
	}

	final void addEvent(MonitoringEvent event) {
		this.events.add(event);
	}

	final long nextEventId() {
		return this.nextEventId.getAndIncrement();
	}

}
