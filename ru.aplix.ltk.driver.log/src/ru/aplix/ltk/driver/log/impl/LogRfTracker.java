package ru.aplix.ltk.driver.log.impl;

import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.collector.RfTracking;
import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfError;
import ru.aplix.ltk.core.source.RfStatusMessage;


final class LogRfTracker implements RfTracker {

	private final LoggingRfProvider<?> provider;
	private LogRfTracker next;
	private LogRfTracker prev;
	private RfTracking tracking;
	private volatile boolean trackingStarted;

	LogRfTracker(LoggingRfProvider<?> provider) {
		this.provider = provider;
	}

	public final LoggingRfProvider<?> getProvider() {
		return this.provider;
	}

	@Override
	public void initRfTracker(RfTracking tracking) {
		this.tracking = tracking;
	}

	@Override
	public void startRfTracking() {
		this.provider.addTracker(this);
		this.trackingStarted = true;
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {
	}

	@Override
	public void stopRfTracking() {
		this.provider.removeTracker(this);
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
		this.provider.logger().error(message, cause);
		updateStatus(new RfError(null, message, cause));
	}


}
