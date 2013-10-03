package ru.aplix.ltk.collector.log.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.osgi.Logger;


final class LoggingRfConnection extends RfConnection {

	private LogRfTracker tracker;

	LoggingRfConnection(
			RfSettings settings,
			RfConnection connection,
			TagLog log,
			Logger logger) {
		super(settings);
		this.tracker = new LogRfTracker(connection.getCollector(), log, logger);
	}

	@Override
	protected RfCollector createCollector() {
		return new LoggingRfCollector(this.tracker);
	}

	@Override
	protected RfSource createSource() {
		return this.tracker;
	}

}
