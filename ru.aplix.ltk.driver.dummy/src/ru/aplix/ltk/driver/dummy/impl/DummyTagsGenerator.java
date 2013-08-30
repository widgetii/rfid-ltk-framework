package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.driver.dummy.DummyRfConnector;


public class DummyTagsGenerator implements Runnable {

	private final DummyRfConnection connection;
	private final long generationPeriod;
	private final long presenceDuration;
	private final long readPeriod;
	private final int maxTags;
	private Thread thread;

	public DummyTagsGenerator(
			DummyRfConnector connector,
			DummyRfConnection connection) {
		this.connection = connection;
		this.generationPeriod = connector.getGenerationPeriod();
		this.presenceDuration = connector.getPresenceDuration();
		this.readPeriod = connector.getReadPeriod();
		this.maxTags = connector.getMaxTags();
	}

	public final long getGenerationPeriod() {
		return this.generationPeriod;
	}

	public final long getPresenceDuration() {
		return this.presenceDuration;
	}

	public final long getReadPeriod() {
		return this.readPeriod;
	}

	public final int getMaxTags() {
		return this.maxTags;
	}

	public final RfReaderContext getContext() {
		return this.connection.getContext();
	}

	public synchronized void start() {
		if (this.thread != null) {
			return;
		}
		this.thread = new Thread(this, "DummyTagsGenerator");
		this.thread.start();
	}

	public synchronized void stop() {
		if (this.thread == null) {
			return;
		}
		this.thread = null;
		notifyAll();
	}

	@Override
	public void run() {

		final DummyTags tags = new DummyTags(this);

		for (;;) {

			final long periodTimeLeft = tags.checkPeriod();

			tags.report();
			if (!delay(Math.min(getReadPeriod(), periodTimeLeft))) {
				return;
			}
		}
	}

	private boolean delay(long timeout) {

		final long end = System.currentTimeMillis() + timeout;
		long left = timeout;

		for (;;) {
			synchronized (this) {
				if (this.thread == null) {
					return false;
				}
				if (left <= 0) {
					return true;
				}
				try {
					wait(left);
				} catch (InterruptedException e) {
					return false;
				}
				left = end - System.currentTimeMillis();
			}
		}
	}

}
