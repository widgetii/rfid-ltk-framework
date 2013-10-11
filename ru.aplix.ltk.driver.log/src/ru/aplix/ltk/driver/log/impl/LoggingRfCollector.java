package ru.aplix.ltk.driver.log.impl;

import static ru.aplix.ltk.core.source.RfSource.EMPTY_RF_SOURCE;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.message.MsgConsumer;


final class LoggingRfCollector extends RfCollector {

	private final LogRfTracker tracker;

	LoggingRfCollector(LogRfTracker tracker) {
		super(EMPTY_RF_SOURCE, tracker);
		this.tracker = tracker;
	}

	@Override
	protected MsgConsumer<
			? super RfTagAppearanceHandle,
			? super RfTagAppearanceMessage> requestTagAppearance(
					MsgConsumer<
							? super RfTagAppearanceHandle,
							? super RfTagAppearanceMessage> consumer,
					long lastEventId) {
		return new LogConsumer(this.tracker, consumer, lastEventId);
	}

}
