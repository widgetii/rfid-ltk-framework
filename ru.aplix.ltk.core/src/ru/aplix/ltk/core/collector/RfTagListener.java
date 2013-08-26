package ru.aplix.ltk.core.collector;

import ru.aplix.ltk.core.reader.RfReadHandle;
import ru.aplix.ltk.core.reader.RfReadMessage;
import ru.aplix.ltk.message.MsgConsumer;


final class RfTagListener
		implements MsgConsumer<RfReadHandle, RfReadMessage> {

	private final RfCollector collector;
	private final RfTracker tracker;
	private RfReadHandle handle;
	private boolean cacheInitialized;

	RfTagListener(RfCollector collector, RfTracker tracker) {
		this.collector = collector;
		this.tracker = tracker != null ? tracker : new DefaultRfTracker();
	}

	@Override
	public void consumerSubscribed(RfReadHandle handle) {
		this.handle = handle;
	}

	@Override
	public void messageReceived(RfReadMessage message) {
		tracker().rfRead(message);
	}

	@Override
	public void consumerUnsubscribed(RfReadHandle handle) {
	}

	final RfTracker tracker() {
		return this.tracker;
	}

	final void start() {
		if (!this.cacheInitialized) {
			tracker().initRfTracker(new RfTracking(this.collector));
			this.cacheInitialized = true;
		}
		this.collector.readerHandle().read(this);
	}

	final void stop() {
		this.handle.unsubscribe();
		this.handle = null;
	}

}
