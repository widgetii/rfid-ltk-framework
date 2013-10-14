package ru.aplix.ltk.monitor;


public interface MonitoringEventListener {

	void eventOccurred(MonitoringEvent event);

	void eventForgotten(MonitoringEvent event);

}
