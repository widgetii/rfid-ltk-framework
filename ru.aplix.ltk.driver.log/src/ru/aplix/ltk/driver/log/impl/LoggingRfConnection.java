package ru.aplix.ltk.driver.log.impl;

import static ru.aplix.ltk.core.source.RfSource.EMPTY_RF_SOURCE;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.source.RfSource;


final class LoggingRfConnection extends RfConnection {

	private LogRfTracker tracker;

	LoggingRfConnection(RfSettings settings, LogRfTracker tracker) {
		super(settings);
		this.tracker = tracker;
	}

	@Override
	protected RfCollector createCollector() {
		return new LoggingRfCollector(this.tracker);
	}

	@Override
	protected RfSource createSource() {
		return EMPTY_RF_SOURCE;
	}

}
