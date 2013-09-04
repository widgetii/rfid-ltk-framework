package ru.aplix.ltk.core.collector;

import java.util.Objects;

import ru.aplix.ltk.core.source.*;


final class RfSourceListener implements RfStatusUpdater, RfDataReceiver {

	private final RfCollector collector;
	private final RfTracker tracker;
	private boolean receivingStatus;
	private boolean cacheInitialized;
	private volatile RfStatusMessage lastStatus;
	private volatile RfStatusMessage lastError;

	RfSourceListener(RfCollector collector, RfTracker tracker) {
		this.collector = collector;
		this.tracker = tracker != null ? tracker : new DefaultRfTracker();
	}

	@Override
	public void updateStatus(RfStatusMessage status) {
		if (!setStatus(status)) {
			// Do not report the same status twice.
			return;
		}
		if (!this.receivingStatus) {
			// Not fully started yet. Do not propagate messages.
			return;
		}
		this.collector.collectorSubscriptions().sendMessage(status);
	}

	@Override
	public void sendRfData(RfDataMessage data) {
		this.collector.noError();
		tracker().rfData(data);
	}

	final RfTracker tracker() {
		return this.tracker;
	}

	final void requestStatus() {
		this.collector.source().requestRfStatus(this);
		this.receivingStatus = true;
	}

	final void requestData() {
		if (!this.cacheInitialized) {
			tracker().initRfTracker(new RfTracking(this.collector));
			this.cacheInitialized = true;
		}
		tracker().startRfTracking();
		this.collector.source().requestRfData(this);
	}

	final void rejectData() {
		try {
			this.collector.source().rejectRfData();
		} finally {
			tracker().stopRfTracking();
		}
	}

	final void rejectStatus() {
		this.lastError = null;
		this.lastStatus = null;
		this.receivingStatus = false;
		this.collector.source().rejectRfStatus();
	}

	final RfStatusMessage lastStatus() {

		final RfStatusMessage error = this.lastError;

		if (error != null) {
			return null;
		}

		return this.lastStatus;
	}

	final synchronized void noError() {
		this.lastError = null;
	}

	private synchronized boolean setStatus(RfStatusMessage status) {
		if (status.getRfStatus().isError()) {
			return setError(status);
		}
		if (this.lastError != null) {
			this.lastError = null;
			this.lastStatus = status;
			return true;
		}
		if (this.lastStatus == null) {
			this.lastStatus = status;
			return true;
		}
		if (!Objects.equals(
				this.lastStatus.getRfReaderId(),
				status.getRfReaderId())) {
			this.lastStatus = status;
			return true;
		}
		return false;
	}

	private boolean setError(RfStatusMessage error) {
		if (this.lastError == null) {
			this.lastError = error;
			return true;
		}
		if (!Objects.equals(
				this.lastError.getErrorMessage(),
				error.getErrorMessage())) {
			this.lastError = error;
			return true;
		}
		if (!Objects.equals(this.lastError.getCause(), error.getCause())) {
			this.lastError = error;
			return true;
		}
		return false;
	}

}