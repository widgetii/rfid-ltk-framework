package ru.aplix.ltk.driver.blackbox.impl;

import static ru.aplix.ltk.core.source.RfSource.EMPTY_RF_SOURCE;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.driver.blackbox.BlackboxRfSettings;


final class BlackboxRfConnection extends RfConnection {

	private final RfCollector collector;

	BlackboxRfConnection(RfCollector collector, BlackboxRfSettings settings) {
		super(settings);
		this.collector = collector;
	}

	@Override
	protected RfSource createSource() {
		return EMPTY_RF_SOURCE;
	}

	@Override
	protected RfCollector createCollector() {
		return this.collector;
	}

}
