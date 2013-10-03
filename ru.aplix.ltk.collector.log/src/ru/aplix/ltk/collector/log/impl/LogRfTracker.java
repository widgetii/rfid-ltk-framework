package ru.aplix.ltk.collector.log.impl;

import ru.aplix.ltk.core.collector.*;
import ru.aplix.ltk.core.source.*;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.osgi.Logger;


final class LogRfTracker implements RfTracker, RfSource {

	private final RfCollector collector;
	private final TagLog log;
	private final Logger logger;
	private RfTracking tracking;
	private RfCollectorHandle statusSubscription;
	private RfTagAppearanceHandle tagHandle;
	private RfStatusUpdater updater;

	LogRfTracker(RfCollector collector, TagLog log, Logger logger) {
		this.collector = collector;
		this.log = log;
		this.logger = logger;
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

	final void error(String message, Throwable cause) {
		this.logger.error(message, cause);
		updateStatus(new RfError(null, message, cause));
	}

	private void updateStatus(RfStatusMessage status) {
		if (this.updater != null) {
			this.updater.updateStatus(status);
		} else {
			this.tracking.updateStatus(status);
		}
	}

	private void logTag(RfTagAppearanceMessage message) {

		final RfTagAppearanceMessage logged;

		try {
			logged = log().log(message);
		} catch (Throwable e) {
			this.tracking.updateTagAppearance(message);
			error("Failed to log message", e);
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
