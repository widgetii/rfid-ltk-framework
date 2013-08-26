package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.reader.RfReaderHandle;
import ru.aplix.ltk.core.reader.RfReaderStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;


final class RfReaderListener
		implements MsgConsumer<RfReaderHandle, RfReaderStatusMessage> {

	private final RfCollector collector;
	private RfReaderHandle handle;

	RfReaderListener(RfCollector collector) {
		this.collector = collector;
	}

	@Override
	public void consumerSubscribed(RfReaderHandle handle) {
		this.handle = handle;
	}

	@Override
	public void messageReceived(RfReaderStatusMessage message) {
		this.collector.collectorSubscriptions().sendMessage(message);
	}

	@Override
	public void consumerUnsubscribed(RfReaderHandle handle) {
	}

	final void start() {
		this.collector.reader().subscribe(this);
	}

	final RfReaderHandle handle() {
		return this.handle;
	}

	final void stop() {
		this.handle.unsubscribe();
		this.handle = null;
	}

}