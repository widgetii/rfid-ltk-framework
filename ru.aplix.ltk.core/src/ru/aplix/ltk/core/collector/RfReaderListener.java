package ru.aplix.ltk.core.collector;

import java.util.Objects;

import ru.aplix.ltk.core.reader.RfReaderHandle;
import ru.aplix.ltk.core.reader.RfReaderStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;


final class RfReaderListener
		implements MsgConsumer<RfReaderHandle, RfReaderStatusMessage> {

	private final RfCollector collector;
	private RfReaderHandle handle;
	private volatile RfReaderStatusMessage lastStatus;
	private volatile RfReaderStatusMessage lastError;

	RfReaderListener(RfCollector collector) {
		this.collector = collector;
	}

	@Override
	public void consumerSubscribed(RfReaderHandle handle) {
		this.handle = handle;
	}

	@Override
	public void messageReceived(RfReaderStatusMessage message) {
		if (!updateStatus(message)) {
			// Do not report the same status twice.
			return;
		}
		this.collector.collectorSubscriptions().sendMessage(message);
	}

	@Override
	public void consumerUnsubscribed(RfReaderHandle handle) {
	}

	final void start() {
		this.lastError = null;
		this.lastStatus = null;
		this.collector.reader().subscribe(this);
	}

	final RfReaderHandle handle() {
		return this.handle;
	}

	final void stop() {
		this.handle.unsubscribe();
		this.handle = null;
	}

	final RfReaderStatusMessage lastStatus() {

		final RfReaderStatusMessage error = this.lastError;

		if (error != null) {
			return error;
		}

		return this.lastStatus;
	}

	final synchronized void noError() {
		this.lastError = null;
	}

	private synchronized boolean updateStatus(RfReaderStatusMessage message) {
		if (message.getRfReaderStatus().isError()) {
			return updateError(message);
		}
		if (this.lastError != null) {
			this.lastError = null;
			this.lastStatus = message;
			return true;
		}
		if (this.lastStatus == null) {
			this.lastStatus = message;
			return true;
		}
		if (!Objects.equals(
				this.lastStatus.getRfReaderId(),
				message.getRfReaderId())) {
			this.lastStatus = message;
			return true;
		}
		return false;
	}

	private boolean updateError(RfReaderStatusMessage error) {
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