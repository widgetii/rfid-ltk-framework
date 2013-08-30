package ru.aplix.ltk.driver.dummy.impl;

import java.util.HashSet;
import java.util.Iterator;

import ru.aplix.ltk.core.reader.RfReaderContext;


final class DummyTags {

	private final DummyTagsGenerator generator;
	private final HashSet<DummyTag> tags = new HashSet<>();
	private boolean generation;
	private int transactionId;
	private long presenceEnd;
	private long periodEnd;

	DummyTags(DummyTagsGenerator generator) {
		this.generator = generator;
	}

	public long checkPeriod() {

		final long now = System.currentTimeMillis();
		final long timeLeft = this.periodEnd - now;

		if (timeLeft > 0) {
			if (!this.generation) {
				return timeLeft;
			}

			final long presenceLeft = this.presenceEnd - now;

			if (presenceLeft <= 0) {
				this.tags.clear();
				return timeLeft;
			}

			return Math.min(timeLeft, presenceLeft);
		}
		if (this.generation) {
			hide();
		} else {
			generate();
		}

		return System.currentTimeMillis() - now;
	}

	private void generate() {
		startPeriod(true);
		this.presenceEnd =
				System.currentTimeMillis()
				+ this.generator.getPresenceDuration();

		int numTags = 1 + (int) (Math.random() * this.generator.getMaxTags());

		do {
			this.tags.add(new DummyTag());
			--numTags;
		} while (numTags > 0);
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