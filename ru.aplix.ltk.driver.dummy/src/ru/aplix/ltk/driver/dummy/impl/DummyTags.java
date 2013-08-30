package ru.aplix.ltk.driver.dummy.impl;

import java.util.HashSet;
import java.util.Iterator;

import ru.aplix.ltk.core.reader.RfReaderContext;


final class DummyTags {

	private final DummyTagsGenerator generator;
	private final HashSet<DummyTag> tags = new HashSet<>();
	private boolean generation;
	private int transactionId;
	private long periodEnd;

	DummyTags(DummyTagsGenerator generator) {
		this.generator = generator;
	}

	public long checkPeriod() {

		final long timeLeft = periodTimeLeft();

		if (timeLeft > 0) {
			return timeLeft;
		}
		if (this.generation) {
			hide();
		} else {
			generate();
		}

		return periodTimeLeft();
	}

	private long periodTimeLeft() {
		return this.periodEnd - System.currentTimeMillis();
	}

	private void generate() {
		startPeriod(true);
		for (int numTags = 1 + (int) (Math.random() + 50);
				numTags > 0;
				--numTags) {
			this.tags.add(new DummyTag());
		}
	}

	private void hide() {
		startPeriod(false);
		this.tags.clear();
	}

	public void report() {
		++this.transactionId;

		final RfReaderContext context = this.generator.getContext();

		if (this.tags.isEmpty()) {
			context.sendData(
					new DummyRfDataMessage(null, this.transactionId, true));
			return;
		}

		final Iterator<DummyTag> it = this.tags.iterator();

		for (;;) {

			final DummyTag tag = it.next();
			final boolean last = !it.hasNext();

			context.sendData(
					new DummyRfDataMessage(tag, this.transactionId, last));
			if (last) {
				break;
			}
		}
	}

	private void startPeriod(boolean generate) {
		this.generation = generate;
		this.periodEnd =
				System.currentTimeMillis()
				+ this.generator.getGenerationPeriod();
	}

}