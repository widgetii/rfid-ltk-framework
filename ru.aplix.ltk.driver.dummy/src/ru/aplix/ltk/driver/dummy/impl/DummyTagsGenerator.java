package ru.aplix.ltk.driver.dummy.impl;

import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.driver.dummy.DummyRfSettings;


public class DummyTagsGenerator implements Runnable {

	private final DummyRfConnection connection;
	private Thread thread;

	public DummyTagsGenerator(DummyRfConnection connection) {
		this.connection = connection;
	}

	public final DummyRfSettings getSettings() {
		return this.connection.getSettings();
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
			if (!delay(Math.min(
					getSettings().getReadPeriod(),
					periodTimeLeft))) {
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
