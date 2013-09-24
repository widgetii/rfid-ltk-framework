package ru.aplix.ltk.collector.log;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;


public final class CyclicLogReader implements Closeable {

	private final CyclicLog log;
	private final FileChannel channel;
	private CyclicLogLock lock;
	private ByteBuffer record;

	CyclicLogReader(CyclicLog log) throws IOException {
		this.log = log;
		this.channel = FileChannel.open(log.getPath(), StandardOpenOption.READ);
		this.record = ByteBuffer.allocate(log.getRecordSize());
	}

	public final CyclicLog log() {
		return this.log;
	}

	public final ByteBuffer record() {
		return this.record;
	}

	public final ByteBuffer read() throws IOException {
		this.channel.read(record());
		return record();
	}

	public boolean seek(CyclicLogFilter filter) throws IOException {
		synchronized (log()) {
			unlock();
			this.lock = this.log.lock();
		}

		if (!middle()) {
			doUnlock();
			return false;
		}

		for (;;) {

			final long position = channel().position();

			record().clear();

			final int cmp = filter.filterRecord(this);

			if (cmp == 0) {
				channel().position(position);
				return true;
			}
			if (cmp < 0) {
				if (!relockFrom(position + log().getRecordSize())) {
					return false;
				}
			}
			if (!middle()) {
				if (cmp < 0) {
					doUnlock();
					return false;
				}
				return true;
			}
		}
	}

	@Override
	public void close() throws IOException {
		unlock();
		this.channel.close();
	}

	private void unlock() {
		if (this.lock != null) {
			doUnlock();
		}
	}

	private boolean relockFrom(long position) throws IOException {

		final long end = this.lock.end();

		synchronized (log()) {
			doUnlock();
			if (end == position) {
				return false;
			}

			final long start;

			if (position + log().getRecordSize() > log().size()) {
				start = 0;
			} else {
				start = position;
			}

			this.lock = log().lock(start, end);
		}

		return true;
	}

	private void doUnlock() {
		this.log.unlock(this.lock);
		this.lock = null;
	}

	private final FileChannel channel() {
		return this.channel;
	}

	private final boolean middle() throws IOException {

		final long start = this.lock.start();
		final long end = this.lock.end();
		final int recordSize = log().getRecordSize();
		final long middle;

		if (start < end) {
			if (end - start <= recordSize) {
				return false;
			}
			middle = (((end - start) / recordSize) >> 1) * recordSize;
		} else {

			final long size = log().size();

			if (size < log().getRecordSize()) {
				return false;
			}

			final long tail = size - start;
			final long mid = (((tail + end) / recordSize) >> 1) * recordSize;

			if (mid < size) {
				middle = start + mid;
			} else {
				middle = mid - tail;
			}
		}

		channel().position(middle);

		return true;
	}

}
