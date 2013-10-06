package ru.aplix.ltk.driver.log.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

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
	private volatile Thread requestThread;
	private volatile TagMessageSender sender;
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

		final TagMessageSender sender = new TagMessageSender();
		final Thread requestThread = new Thread(this);

		synchronized (this) {
			this.sender = sender;
			this.requestThread = requestThread;
		}

		sender.start();
		requestThread.start();
	}

	@Override
	public void messageReceived(RfTagAppearanceMessage message) {
		if (isPatthrough()) {
			getProxied().messageReceived(message);
		}
	}

	@Override
	public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
		synchronized (this) {
			stopSending(true);
		}
		super.consumerUnsubscribed(handle);
	}

	@Override
	public synchronized void readerExhausted(CyclicLogReader reader) {
		this.passthrough = true;
		stopSending(false);
	}

	@Override
	public void run() {
		try {
			requestMessages();
		} catch (Throwable e) {
			this.tracker.error("Failed to read messages", e);
		}
	}

	private synchronized boolean isPatthrough() {
		return this.passthrough;
	}

	private void requestMessages() throws IOException {
		try (CyclicLogReader reader = this.tracker.log().read(this)) {
			if (reader.seek(new TagEventsFilter(this.lastEventId))) {
				sendMessages(reader);
			}
		}
	}

	private void sendMessages(CyclicLogReader reader) throws IOException {
		for (;;) {
			if (this.requestThread == null) {
				break;
			}

			final ByteBuffer record = reader.pull();

			if (record == null) {
				break;
			}

			final TagMessageSender sender = this.sender;

			if (sender == null) {
				break;
			}
			try {
				sender.messages.put(new RfTagAppearanceRecord(record));
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	private void stopSending(boolean shutdown) {
		this.requestThread = null;

		final TagMessageSender sender = this.sender;

		if (sender != null) {
			sender.stopSending(shutdown);
		}
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

	private final class TagMessageSender extends Thread {

		private final ArrayBlockingQueue<RfTagAppearanceRecord> messages =
				new ArrayBlockingQueue<>(16);

		@Override
		public void run() {
			for (;;) {

				final RfTagAppearanceRecord message;

				try {
					message = this.messages.take();
				} catch (InterruptedException e) {
					break;
				}
				if (message.isStop()) {
					break;
				}

				try {
					getProxied().messageReceived(message);
				} catch (Throwable e) {
					LogConsumer.this.tracker.error("Failed to send tag", e);
				}
			}
		}

		private void stopSending(boolean shutdown) {
			if (shutdown) {
				this.messages.clear();
			}
			this.messages.add(RfTagAppearanceRecord.STOP);
		}

	}

}
