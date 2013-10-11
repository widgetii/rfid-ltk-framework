package ru.aplix.ltk.driver.log.impl;

import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.collector.RfTracking;
import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfError;
import ru.aplix.ltk.core.source.RfStatusMessage;


final class LogRfTracker implements RfTracker {

	private final LogRfTarget<?> target;
	private LogRfTracker next;
	private LogRfTracker prev;
	private RfTracking tracking;
	private volatile boolean trackingStarted;

	LogRfTracker(LogRfTarget<?> target) {
		this.target = target;
	}

	public final LogRfTarget<?> getTarget() {
		return this.target;
	}

	@Override
	public void initRfTracker(RfTracking tracking) {
		this.tracking = tracking;
	}

	@Override
	public void startRfTracking() {
		this.target.addTracker(this);
		this.trackingStarted = true;
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {
	}

	@Override
	public void stopRfTracking() {
		this.target.removeTracker(this);
		this.trackingStarted = false;
	}

	final LogRfTracker getNext() {
		return this.next;
	}

	final void setNext(LogRfTracker next) {
		this.next = next;
	}

	final LogRfTracker getPrev() {
		return this.prev;
	}

	final void setPrev(LogRfTracker prev) {
		this.prev = prev;
	}

	final void updateStatus(RfStatusMessage status) {
		this.tracking.updateStatus(status);
	}

	final void updateTagAppearance(RfTagAppearanceMessage tagAppearance) {
		if (this.trackingStarted) {
			try {
				this.tracking.updateTagAppearance(tagAppearance);
			} catch (Throwable e) {
				error("Failed to update tag appearance", e);
			}
		}
	}

	final void error(String message, Throwable cause) {
		this.target.logger().error(message, cause);
		updateStatus(new RfError(null, message, cause));
	}


}
