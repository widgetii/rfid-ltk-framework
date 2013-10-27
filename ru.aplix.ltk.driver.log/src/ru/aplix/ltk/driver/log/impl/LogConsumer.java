package ru.aplix.ltk.driver.log.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.driver.log.CyclicLogClient;
import ru.aplix.ltk.driver.log.CyclicLogFilter;
import ru.aplix.ltk.driver.log.CyclicLogReader;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgProxy;


final class LogConsumer
		extends MsgProxy<RfTagAppearanceHandle, RfTagAppearanceMessage>
		implements Runnable, CyclicLogClient {

	private final LogRfTracker tracker;
	private final long lastEventId;
	private volatile Thread sender;
	private boolean passthrough;

	LogConsumer(
			LogRfTracker tracker,
			MsgConsumer<
					? super RfTagAppearanceHandle,
					? super RfTagAppearanceMessage> consumer,
			long lastEventId) {
		super(consumer);
		this.tracker = tracker;
		this.lastEventId = lastEventId;
	}

	@Override
	public void consumerSubscribed(RfTagAppearanceHandle handle) {
		super.consumerSubscribed(handle);

		final Thread sender = new Thread(this);

		synchronized (this) {
			this.sender = sender;
		}

		sender.start();
	}

	@Override
	public void messageReceived(RfTagAppearanceMessage message) {
		if (isPatthrough()) {
			updateTagAppearance(message);
		}
	}

	@Override
	public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
		synchronized (this) {
			stopSending();
		}
		super.consumerUnsubscribed(handle);
	}

	@Override
	public synchronized void readerExhausted(CyclicLogReader reader) {
		this.passthrough = true;
		stopSending();
	}

	@Override
	public void run() {
		try {
			requestMessages();
		} catch (Throwable e) {
			this.tracker.error("Failed to read messages", e);
			synchronized (this) {
				this.passthrough = true;
			}
		}
	}

	private synchronized boolean isPatthrough() {
		return this.passthrough;
	}

	private void updateTagAppearance(RfTagAppearanceMessage tagAppearance) {
		getProxied().messageReceived(tagAppearance);
	}

	private void requestMessages() throws IOException {
		try (CyclicLogReader reader =
				this.tracker.getTarget().log().read(this)) {
			if (reader.seek(new TagEventsFilter(this.lastEventId))) {
				sendMessages(reader);
			}
		}
	}

	private void sendMessages(CyclicLogReader reader) throws IOException {
		for (;;) {
			if (this.sender == null) {
				break;
			}

			final ByteBuffer record = reader.pull();

			if (record == null) {
				break;
			}

			try {
				updateTagAppearance(new RfTagAppearanceRecord(record));
			} catch (Throwable e) {
				this.tracker.error("Failed to send tag", e);
			}
		}
	}

	private void stopSending() {
		this.sender = null;
	}

	private static final class TagEventsFilter implements CyclicLogFilter  {

		private final long lastEventId;

		TagEventsFilter(long lastEventId) {
			this.lastEventId = lastEventId;
		}

		@Override
		public int filterRecord(CyclicLogReader reader) throws IOException {

			final ByteBuffer rec = reader.record();

			rec.rewind().limit(8);
			reader.channel().read(rec);
			rec.rewind();

			final long eventId = rec.getLong();

			if (eventId >= this.lastEventId) {
				return 1;
			}

			return -1;
		}

	}

}
