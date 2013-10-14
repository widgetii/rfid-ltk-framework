package ru.aplix.ltk.monitor;


/**
 * Base interface of monitoring events listeners.
 *
 * <p>Each monitoring type has its own listener interface. Listeners
 * {@link Monitoring#addListener(MonitoringListener) registered} in monitoring
 * instances receive notifications specific to that monitoring type.</p>
 */
public interface MonitoringListener {

}
