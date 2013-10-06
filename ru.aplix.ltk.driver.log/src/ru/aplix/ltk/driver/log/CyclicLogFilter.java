package ru.aplix.ltk.driver.log;

import java.io.IOException;


public interface CyclicLogFilter {

	int filterRecord(CyclicLogReader reader) throws IOException;

}
