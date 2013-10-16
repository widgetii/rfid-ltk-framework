package ru.aplix.ltk.monitor;


public interface MonitoringEventListener {

	void eventOccurred(
			MonitoringEvent event,
			MonitoringEvent.Content occurred);

	void eventForgotten(
			MonitoringEvent event,
			MonitoringEvent.Content forgotten);

}
