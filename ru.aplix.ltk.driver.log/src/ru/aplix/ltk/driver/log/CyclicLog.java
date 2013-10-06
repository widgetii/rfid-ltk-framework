package ru.aplix.ltk.driver.log;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;


public class CyclicLog implements Closeable {

	private final Path path;
	private final Path positionPath;
	private final FileChannel channel;
	private final FileChannel positionChannel;
	private final ByteBuffer record;
	private final ByteBuffer positionRecord;
	private final long maxSize;
	private CyclicLogLock lock;
	private long lockStart;
	private long lockEnd;

	public CyclicLog(CyclicLogConfig config) throws IOException {
		this.path = config.getPath();
		this.positionPath = config.getPositionPath();
		this.maxSize = config.getMaxRecords() * config.getRecordSize();
		this.record = ByteBuffer.allocate(config.getRecordSize());
		this.positionRecord = ByteBuffer.allocate(8);
		this.channel = FileChannel.open(getPath(), config.openOptions());
		this.positionChannel =
				FileChannel.open(getPositionPath(), config.openOptions());
		initPosition();
	}

	public final Path getPath() {
		return this.path;
	}

	public final Path getPositionPath() {
		return this.positionPath;
	}

	public final int getRecordSize() {
		return record().capacity();
	}

	public final long getMaxSize() {
		return this.maxSize;
	}

	public final ByteBuffer record() {
		return this.record;
	}

	public final FileChannel channel() {
		return this.channel;
	}

	public final long size() throws IOException {
		return channel().size();
	}

	public final FileChannel positionChannel() {
		return this.positionChannel;
	}

	public void write() throws IOException {
		record().clear();
		synchronized (this) {
			waitToWrite();
			channel().write(record());
			updatePosition();
			storePosition();
		}
		record().clear();
	}

	public final CyclicLogReader read() throws IOException {
		return read(null);
	}

	public final CyclicLogReader read(
			CyclicLogClient client)
	throws IOException {
		return new CyclicLogReader(this, client);
	}

	@Override
	public void close() throws IOException {
		try {
			this.channel.close();
		} finally {
			this.positionChannel.close();
		}
	}

	public void delete() throws IOException {
		close();
		getPath().toFile().delete();
		getPositionPath().toFile().delete();
	}

	synchronized CyclicLogLock lock() throws IOException {

		final long position = channel().position();

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
		}
		if (this.lock == null) {
			notifyAll();
			return;
		}

		final long[] bounds = this.lock.evalBounds();

		this.lockStart = bounds[0];
		this.lockEnd = bounds[1];

		notifyAll();
	}

	private void initPosition() throws IOException {
		positionChannel().read(this.positionRecord);
		if (this.positionRecord.hasRemaining()) {
			channel().truncate(0);
			return;
		}
		this.positionRecord.clear();

		final long logPosition = this.positionRecord.getLong();

		if (logPosition >= getMaxSize()) {
			// Truncate the log.
			channel().truncate(getMaxSize());
			return;
		}
		if (logPosition <= size()) {
			channel().position(logPosition);
		}
	}

	private boolean updatePosition() throws IOException {
		if (channel().position() >= getMaxSize()) {
			// Truncate the log.
			if (size() > getMaxSize()) {
				waitToTruncate();
				channel().truncate(getMaxSize());
			}
			channel().position(0);
		}
		return true;
	}

	private void storePosition() throws IOException {
		this.positionRecord.clear();
		this.positionRecord.putLong(channel().position());
		this.positionRecord.clear();
		positionChannel().position(0).write(this.positionRecord);
	}

	private void waitToWrite() throws IOException {
		for (;;) {
			if (this.lock == null) {
				return;
			}

			final long logPosition = channel().position();

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
