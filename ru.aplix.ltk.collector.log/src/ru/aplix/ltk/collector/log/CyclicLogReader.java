package ru.aplix.ltk.collector.log;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;


public final class CyclicLogReader implements Closeable {

	private final CyclicLog log;
	private final CyclicLogClient client;
	private final FileChannel channel;
	private CyclicLogLock lock;
	private ByteBuffer record;

	CyclicLogReader(
			CyclicLog log,
			CyclicLogClient client)
	throws IOException {
		this.log = log;
		this.client = client;
		this.channel = FileChannel.open(log.getPath(), StandardOpenOption.READ);
		this.record = ByteBuffer.allocate(log.getRecordSize());
	}

	public final CyclicLog log() {
		return this.log;
	}

	public final ByteBuffer record() {
		return this.record;
	}

	public final FileChannel channel() {
		return this.channel;
	}

	public final ByteBuffer read() throws IOException {

		final ByteBuffer record = record();

		record.clear();
		channel().read(record);
		record.clear();

		return record;
	}

	public boolean seek(CyclicLogFilter filter) throws IOException {
		synchronized (log()) {
			unlock();
			this.lock = this.log.lock();
		}

		long lastGreater = -1;
		long start = this.lock.start();
		long end = this.lock.end();

		if (!middle(start, end)) {
			seekFailed();
			return false;
		}

		for (;;) {

			final long position = channel().position();
			final int cmp = filter.filterRecord(this);

			if (cmp == 0) {
				relockFrom(position);
				channel().position(position);
				return true;
			}
			if (cmp > 0) {
				lastGreater = end = position;
			} else {
				start = nextRecord(position, end);
				if (start < 0) {
					break;
				}
				relockFrom(start);
			}
			if (position == start) {
				break;
			}
			if (!middle(start, end)) {
				break;
			}
		}

		if (lastGreater < 0) {
			seekFailed();
			return false;
		}
		relockFrom(lastGreater);
		channel().position(lastGreater);

		return true;
	}

	public ByteBuffer pull() throws IOException {
		if (this.lock == null) {
			throw new IllegalStateException(
					"Can't read from cyclic log without locking."
					+ " Invoke seek() first");
		}

		final ByteBuffer result = read();

		synchronized (log()) {
			checkExhaust(channel().position());
		}

		return result;
	}

	@Override
	public void close() throws IOException {
		unlock();
		this.channel.close();
	}

	private void unlock() {
		if (this.lock != null) {
			removeLock();
		}
	}

	private long nextRecord(long start, long end) throws IOException {
		if (start == end) {
			return -1;
		}

		final int recordSize = log().getRecordSize();
		final long position = start + recordSize;

		if (position == end) {
			return - 1;
		}
		if (position + recordSize > log().size()) {
			if (end == 0) {
				return -1;
			}
			return 0;
		}

		return position;
	}

	private void relockFrom(long position) throws IOException {
		synchronized (log()) {
			removeLock();

			final long start;

			if (position + log().getRecordSize() > log().size()) {
				start = 0;
			} else {
				start = position;
			}

			this.lock = log().lock(start, log().channel().position());
		}
	}

	private void seekFailed() throws IOException {
		synchronized (log()) {
			checkExhaust(this.lock.end());
		}
	}

	private void checkExhaust(long position) throws IOException {

		final long logPosition = log().channel().position();

		if (position != logPosition) {
			relockFrom(position);
		} else {
			removeLock();
			if (this.client != null) {
				this.client.readerExhausted(this);
			}
		}
	}

	private void removeLock() {
		this.log.unlock(this.lock);
		this.lock = null;
	}

	private final boolean middle(long start, long end) throws IOException {

		final int recordSize = log().getRecordSize();
		final long middle;

		if (start < end) {
			if (end - start < recordSize) {
				return false;
			}
			middle = start + (((end - start) / recordSize) >> 1) * recordSize;
		} else {

			final long size = log().size();

			if (size < log().getRecordSize()) {
				return false;
			}

			final long tail = size - start;
			final long mid = (((tail + end) / recordSize) >> 1) * recordSize;

			if (mid < tail) {
				middle = start + mid;
			} else {
				middle = mid - tail;
			}
		}

		channel().position(middle);

		return true;
	}

}
