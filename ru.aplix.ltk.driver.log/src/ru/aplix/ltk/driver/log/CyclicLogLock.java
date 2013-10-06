package ru.aplix.ltk.driver.log;


final class CyclicLogLock {

	private CyclicLogLock prev;
	private CyclicLogLock next;
	private final long start;
	private final long end;

	CyclicLogLock(CyclicLogLock prev, long start, long end) {
		this.prev = prev;
		this.start = start;
		this.end = end;
		if (prev != null) {
			prev.next = this;
		}
	}

	public final CyclicLogLock prev() {
		return this.prev;
	}

	public final long start() {
		return this.start;
	}

	public final long end() {
		return this.end;
	}

	public long[] evalBounds() {
		if (this.prev == null) {
			return new long[] {start(), end()};
		}

		final long[] bounds = prev().evalBounds();

		updateBounds(bounds);

		return bounds;
	}

	public void updateBounds(long[] bounds) {

		final long start = bounds[0];
		final long end = bounds[1];

		if (start() < start) {
			bounds[0] = start();
		}
		if (start < end) {
			if (start() >= end()) {
				bounds[1] = end();
			} else if (end() > end) {
				bounds[1] = end();
			}
		} else if (start() >= end()) {
			if (end() > end) {
				bounds[1] = end();
			}
		}
	}

	@Override
	public String toString() {
		if (this.start < this.end) {
			return "CyclicLogLock[" + this.start + "..." + this.end + ']';
		}
		if (this.start == this.end) {
			return "CyclicLogLock[..." + this.start + "...]";
		}
		return "CyclicLogLock[..." + this.end + "," + this.start + "...]";
	}

	final CyclicLogLock remove() {

		final CyclicLogLock next = this.next;

		if (this.prev != null) {
			this.prev.next = next;
		}
		if (next != null) {
			next.prev = this.prev;
		}
		this.prev = null;
		this.next = null;

		return next;
	}

}
