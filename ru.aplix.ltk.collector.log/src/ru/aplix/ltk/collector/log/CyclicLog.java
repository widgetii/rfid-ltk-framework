package ru.aplix.ltk.collector.log;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;


public class CyclicLog implements Closeable {

	private final Path path;
	private final FileChannel logChannel;
	private final FileChannel positionChannel;
	private final ByteBuffer record;
	private final ByteBuffer positionRecord;
	private final LongBuffer positionValue;
	private final long maxSize;
	private CyclicLogLock lock;
	private long lockStart;
	private long lockEnd;

	public CyclicLog(CyclicLogConfig config) throws IOException {
		this.path = config.getPath();
		this.maxSize = config.getMaxRecords() * config.getRecordSize();
		this.record = ByteBuffer.allocate(config.getRecordSize());
		this.positionRecord = ByteBuffer.allocate(8);
		this.positionValue = this.positionRecord.asLongBuffer();
		this.logChannel = config.openLog();
		this.positionChannel = config.openPosition();
		initPosition();
	}

	public final Path getPath() {
		return this.path;
	}

	public final int getRecordSize() {
		return record().limit();
	}

	public final long getMaxSize() {
		return this.maxSize;
	}

	public final ByteBuffer record() {
		return this.record;
	}

	public final FileChannel logChannel() {
		return this.logChannel;
	}

	public final long size() throws IOException {
		return logChannel().size();
	}

	public final FileChannel positionChannel() {
		return this.positionChannel;
	}

	public boolean write() throws IOException {
		record().clear();
		synchronized (this) {
			waitToWrite();
			logChannel().write(record());
			updatePosition();
		}
		storePosition();
		record().clear();
		return true;
	}

	public final CyclicLogReader read() throws IOException {
		return new CyclicLogReader(this);
	}

	@Override
	public void close() throws IOException {
		this.logChannel.close();
		this.positionChannel.close();
	}

	synchronized CyclicLogLock lock() throws IOException {

		final long position = logChannel().position();

		if (position + getRecordSize() >= size()) {
			return lock(0, position);
		}

		return lock(position, position);
	}

	synchronized CyclicLogLock lock(long start, long end) {
		this.lock = new CyclicLogLock(this.lock, start, end);

		if (this.lock.prev() == null) {
			this.lockStart = start;
			this.lockEnd = end;
		} else {

			final long[] bounds = new long[] {this.lockStart, this.lockEnd};

			this.lock.updateBounds(bounds);
			this.lockStart = bounds[0];
			this.lockEnd = bounds[1];
		}

		return this.lock;
	}

	synchronized void unlock(CyclicLogLock lock) {

		final CyclicLogLock next = lock.remove();

		if (this.lock == lock) {
			this.lock = next;
			if (next == null) {
				notifyAll();
				return;
			}
		}

		final long[] bounds = this.lock.evalBounds();

		this.lockStart = bounds[0];
		this.lockEnd = bounds[1];

		notifyAll();
	}

	private void initPosition() throws IOException {
		positionChannel().read(this.positionRecord);
		if (this.positionRecord.position() != 8) {
			logChannel().truncate(0);
			return;
		}

		final long logPosition = this.positionValue.get();

		if (logPosition >= getMaxSize()) {
			// Truncate the log.
			logChannel().truncate(getMaxSize());
			return;
		}
		if (logPosition <= size()) {
			logChannel().position(logPosition);
		}
	}

	private boolean updatePosition() throws IOException {
		if (logChannel().position() >= getMaxSize()) {
			// Truncate the log.
			if (size() > getMaxSize()) {
				waitToTruncate();
				logChannel().truncate(getMaxSize());
			}
			logChannel().position(0);
		}
		return true;
	}

	private void storePosition() throws IOException {
		this.positionValue.clear();
		this.positionValue.put(logChannel().position());
		this.positionRecord.clear();
		positionChannel().write(this.positionRecord);
	}

	private void waitToWrite() throws IOException {
		for (;;) {
			if (this.lock == null) {
				return;
			}

			final long logPosition = logChannel().position();

			if (this.lockStart >= this.lockEnd) {
				if (this.lockStart >= logPosition + getRecordSize()
						&& this.lockEnd <= logPosition) {
					return;
				}
			} else {
				if (this.lockStart >= logPosition + getRecordSize()
						|| this.lockEnd <= logPosition) {
					return;
				}
			}
			if (this.lockStart >= logPosition + getRecordSize()) {
				if (this.lockStart < this.lockEnd
						|| this.lockEnd <= logPosition) {
					return;
				}
			}
			try {
				wait();
			} catch (InterruptedException e) {
				throw closeByInterrupt(e);
			}
		}
	}

	private void waitToTruncate() throws IOException {
		for (;;) {
			if (this.lock == null) {
				return;
			}
			if (this.lockStart < this.lockEnd && this.lockEnd <= getMaxSize()) {
				return;
			}
			try {
				wait();
			} catch (InterruptedException e) {
				throw closeByInterrupt(e);
			}
		}
	}

	private ClosedByInterruptException closeByInterrupt(
			InterruptedException cause)
	throws IOException {
		close();

		final ClosedByInterruptException ex =
				new ClosedByInterruptException();

		ex.initCause(cause);

		return ex;
	}

}
