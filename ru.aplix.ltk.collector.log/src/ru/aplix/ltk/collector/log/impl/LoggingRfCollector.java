package ru.aplix.ltk.collector.log.impl;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.message.MsgConsumer;


final class LoggingRfCollector extends RfCollector {

	private final LogRfTracker tracker;

	LoggingRfCollector(LogRfTracker tracker) {
		super(tracker, tracker);
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
