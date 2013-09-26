package ru.aplix.ltk.collector.log.impl;

import ru.aplix.ltk.core.collector.*;
import ru.aplix.ltk.core.source.*;
import ru.aplix.ltk.message.MsgConsumer;


public class LogRfTracker implements RfTracker, RfSource {

	private final RfCollector collector;
	private final TagLog log;
	private RfTracking tracking;
	private RfCollectorHandle statusSubscription;
	private RfTagAppearanceHandle tagHandle;

	public LogRfTracker(RfCollector collector, TagLog log) {
		this.collector = collector;
		this.log = log;
	}

	@Override
	public void requestRfStatus(RfStatusUpdater updater) {
		this.statusSubscription =
				this.collector.subscribe(new LogStatusListener(updater));
	}

	@Override
	public void requestRfData(RfDataReceiver receiver) {
	}

	@Override
	public void rejectRfData() {
	}

	@Override
	public void rejectRfStatus() {
		this.statusSubscription.unsubscribe();
		this.statusSubscription = null;
	}

	@Override
	public void initRfTracker(RfTracking tracking) {
		this.tracking = tracking;

	}

	@Override
	public void startRfTracking() {
		this.tagHandle = this.statusSubscription.requestTagAppearance(
				new LogTagListener());
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {
	}

	@Override
	public void stopRfTracking() {
		this.tagHandle.unsubscribe();
		this.tagHandle = null;
	}

	private void logTag(RfTagAppearanceMessage message) {

		final RfTagAppearanceRecord record;

		try {
			record = this.log.write(message);
		} catch (Throwable e) {
			this.tracking.updateTagAppearance(message);
			this.tracking.updateStatus(
					new RfError(null, "Failed to log message", e));
			return;
		}

		this.tracking.updateTagAppearance(record);
	}

	private static final class LogStatusListener
			implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

		private final RfStatusUpdater updater;

		LogStatusListener(RfStatusUpdater updater) {
			this.updater = updater;
		}

		@Override
		public void consumerSubscribed(RfCollectorHandle handle) {
		}

		@Override
		public void messageReceived(RfStatusMessage message) {
			this.updater.updateStatus(message);
		}

		@Override
		public void consumerUnsubscribed(RfCollectorHandle handle) {
		}

	}

	private final class LogTagListener implements MsgConsumer<
			RfTagAppearanceHandle,
			RfTagAppearanceMessage> {

		@Override
		public void consumerSubscribed(RfTagAppearanceHandle handle) {
		}

		@Override
		public void messageReceived(RfTagAppearanceMessage message) {
			logTag(message);
		}

		@Override
		public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
		}

	}

}
