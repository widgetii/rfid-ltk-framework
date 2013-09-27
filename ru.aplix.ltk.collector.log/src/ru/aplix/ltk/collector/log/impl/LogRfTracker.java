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
	private RfStatusUpdater updater;

	public LogRfTracker(RfCollector collector, TagLog log) {
		this.collector = collector;
		this.log = log;
	}

	public final TagLog log() {
		return this.log;
	}

	@Override
	public void requestRfStatus(RfStatusUpdater updater) {
		this.updater = updater;
		this.statusSubscription =
				this.collector.subscribe(new LogStatusListener());
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
		this.updater = null;
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

	final void updateStatus(RfStatusMessage status) {
		this.updater.updateStatus(status);
	}

	private void logTag(RfTagAppearanceMessage message) {

		final RfTagAppearanceMessage logged;

		try {
			logged = log().log(message);
		} catch (Throwable e) {
			this.tracking.updateTagAppearance(message);
			this.tracking.updateStatus(
					new RfError(null, "Failed to log message", e));
			return;
		}

		this.tracking.updateTagAppearance(logged);
	}

	private final class LogStatusListener
			implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

		@Override
		public void consumerSubscribed(RfCollectorHandle handle) {
		}

		@Override
		public void messageReceived(RfStatusMessage message) {
			updateStatus(message);
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
