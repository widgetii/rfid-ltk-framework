package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.reader.RfDataHandle;
import ru.aplix.ltk.core.reader.RfDataMessage;
import ru.aplix.ltk.message.MsgConsumer;


final class RfDataListener
		implements MsgConsumer<RfDataHandle, RfDataMessage> {

	private final RfCollector collector;
	private final RfTracker tracker;
	private RfDataHandle handle;
	private boolean cacheInitialized;

	RfDataListener(RfCollector collector, RfTracker tracker) {
		this.collector = collector;
		this.tracker = tracker != null ? tracker : new DefaultRfTracker();
	}

	@Override
	public void consumerSubscribed(RfDataHandle handle) {
		this.handle = handle;
	}

	@Override
	public void messageReceived(RfDataMessage message) {
		this.collector.noError();
		tracker().rfData(message);
	}

	@Override
	public void consumerUnsubscribed(RfDataHandle handle) {
	}

	final RfTracker tracker() {
		return this.tracker;
	}

	final void start() {
		if (!this.cacheInitialized) {
			tracker().initRfTracker(new RfTracking(this.collector));
			this.cacheInitialized = true;
		}
		this.collector.readerHandle().requestData(this);
	}

	final void stop() {
		this.handle.unsubscribe();
		this.handle = null;
	}

}
