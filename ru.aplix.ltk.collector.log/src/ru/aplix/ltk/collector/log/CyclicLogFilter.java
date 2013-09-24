package ru.aplix.ltk.collector.log;


public interface CyclicLogFilter {

	int filterRecord(CyclicLogReader reader);

}
