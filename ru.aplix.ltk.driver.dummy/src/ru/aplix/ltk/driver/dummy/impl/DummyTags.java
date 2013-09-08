package ru.aplix.ltk.driver.dummy.impl;

import static java.lang.System.currentTimeMillis;

import java.util.HashSet;
import java.util.Iterator;

import ru.aplix.ltk.core.reader.RfReaderContext;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.driver.dummy.DummyRfSettings;


final class DummyTags {

	private final DummyTagsGenerator generator;
	private final HashSet<RfTag> tags = new HashSet<>();
	private boolean generation;
	private int transactionId;
	private long presenceEnd;
	private long periodEnd;

	DummyTags(DummyTagsGenerator generator) {
		this.generator = generator;
	}

	public final DummyRfSettings getSettings() {
		return this.generator.getSettings();
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
				currentTimeMillis() + getSettings().getPresenceDuration();

		int numTags = 1 + (int) (Math.random() * getSettings().getMaxTags());

		do {
			this.tags.add(randomTag());
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
			context.sendRfData(
					new DummyRfDataMessage(null, this.transactionId, true));
			return;
		}

		final Iterator<RfTag> it = this.tags.iterator();

		for (;;) {

			final RfTag tag = it.next();
			final boolean last = !it.hasNext();

			context.sendRfData(
					new DummyRfDataMessage(tag, this.transactionId, last));

			if (last) {
				break;
			}
		}
	}

	private void startPeriod(boolean generate) {
		this.generation = generate;
		this.periodEnd =
				currentTimeMillis() + getSettings().getGenerationPeriod();
	}

	private static RfTag randomTag() {
		return dummyTag(0x100000000L + (long) (Math.random() * 0xffffffffL));
	}

	private static RfTag dummyTag(long value) {
		return new RfTag(new byte[] {
			(byte) (value & 0xff),
			(byte) ((value >>> 8) & 0xff),
			(byte) ((value >>> 16) & 0xff),
			(byte) ((value >>> 24) & 0xff),
			(byte) ((value >>> 32) & 0xff),
			(byte) ((value >>> 40) & 0xff),
			(byte) ((value >>> 48) & 0xff),
			(byte) ((value >>> 56) & 0xff),
		});
	}

}