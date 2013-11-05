package ru.aplix.ltk.core.collector;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.aplix.ltk.core.source.*;


final class RfSourceListener implements RfStatusUpdater, RfDataReceiver {

	private final RfCollector collector;
	private final RfTracker tracker;
	private boolean receivingStatus;
	private boolean cacheInitialized;
	private volatile RfStatusMessage lastStatus;
	private volatile RfStatusMessage lastError;
	private final AtomicBoolean initialEvent = new AtomicBoolean();

	RfSourceListener(RfCollector collector, RfTracker tracker) {
		this.collector = collector;
		this.tracker = tracker != null ? tracker : new DefaultRfTracker();
	}

	RfSourceListener(RfCollector collector, RfTrackingPolicy trackingPolicy) {
		this.collector = collector;
		this.tracker =
				trackingPolicy != null
				? trackingPolicy.newTracker(collector)
				: new DefaultRfTracker();
	}

	@Override
	public void updateStatus(RfStatusMessage status) {
		if (!setStatus(status)) {
			// Do not report the same status twice.
			return;
		}
		reportStatus(status);
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

	final void initiateEvents() {
		this.initialEvent.set(true);
	}

	final boolean nextEventIsInitial() {
		return this.initialEvent.compareAndSet(true, false);
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
		this.initialEvent.set(false);
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

	final void noError() {

		final RfStatusMessage lastStatus;

		synchronized (this) {
			if (this.lastError == null) {
				lastStatus = null;
			} else {
				this.lastError = null;
				lastStatus = this.lastStatus;
			}
		}
		if (lastStatus != null) {
			reportStatus(lastStatus);
		}
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

	private void reportStatus(RfStatusMessage status) {
		if (!this.receivingStatus) {
			// Not fully started yet. Do not propagate messages.
			return;
		}
		this.collector.collectorSubscriptions().sendMessage(status);
	}

}