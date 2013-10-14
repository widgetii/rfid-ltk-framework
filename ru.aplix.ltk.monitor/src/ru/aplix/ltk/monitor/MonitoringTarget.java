package ru.aplix.ltk.monitor;


/**
 * Monitoring target representation.
 *
 * @param <M> a type of monitoring of this target.
 */
public abstract class MonitoringTarget<M extends Monitoring<?>> {

	/**
	 * Creates new monitoring instance.
	 *
	 * <p>This method is called by {@link MonitoringContext#createMonitoring(
	 * MonitoringTarget)} method and should not be used otherwise.</p>
	 *
	 * <p>A returned instance initialization should be performed by
	 * {@link Monitoring#init()} method.</p>
	 *
	 * @return new monitoring instance.
	 */
	protected abstract M newMonitoring();

}
