package ru.aplix.ltk.collector.log;

import java.io.IOException;


public interface CyclicLogFilter {

	int filterRecord(CyclicLogReader reader) throws IOException;

}
