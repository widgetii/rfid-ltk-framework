package ru.aplix.ltk.collector.log;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;


public class CyclicLogConfig {

	private static final byte SYNC_MASK = 1;
	private static final byte SYNC_META_MASK = 2;

	private final Path path;
	private final int recordSize;
	private long maxRecords = 1;
	private byte sync;

	public CyclicLogConfig(Path path, int recordSize) {
		requireNonNull(path, "Cyclic log path not specified");
		if (recordSize <= 0) {
			throw new IllegalArgumentException(
					"Cyclig log record size should be positive: " + recordSize);
		}
		this.path = path;
		this.recordSize = recordSize;
	}

	public final Path getPath() {
		return this.path;
	}

	public final int getRecordSize() {
		return this.recordSize;
	}

	public final long getMaxRecords() {
		return this.maxRecords;
	}

	public CyclicLogConfig setMaxRecords(long maxRecords) {
		if (maxRecords < 0) {
			throw new IllegalStateException(
					"Too few records in cyclic log: " + maxRecords);
		}
		this.maxRecords = maxRecords;
		return this;
	}

	public final boolean isSync() {
		return this.sync > 0;
	}

	public CyclicLogConfig setSync(boolean sync) {
		if (sync) {
			this.sync |= SYNC_MASK;
		} else {
			this.sync &= ~SYNC_MASK;
		}
		return this;
	}

	public final boolean isSyncMeta() {
		return (this.sync & SYNC_META_MASK) != 0;
	}

	public CyclicLogConfig setSyncMeta(boolean syncMeta) {
		if (syncMeta) {
			this.sync |= SYNC_META_MASK;
		} else {
			this.sync &= ~SYNC_META_MASK;
		}
		return this;
	}

	protected FileChannel openLog() throws IOException {
		return FileChannel.open(getPath(), openOptions());
	}

	protected FileChannel openPosition() throws IOException {

		final Path path =
				getPath().resolveSibling(getPath().getFileName() + ".pos");

		return FileChannel.open(path, openOptions());
	}

	protected Set<? extends OpenOption> openOptions() {

		final HashSet<OpenOption> options = new HashSet<>(4);

		options.add(StandardOpenOption.CREATE);
		options.add(StandardOpenOption.READ);
		options.add(StandardOpenOption.WRITE);

		if (isSync()) {
			if (isSyncMeta()) {
				options.add(StandardOpenOption.SYNC);
			} else {
				options.add(StandardOpenOption.DSYNC);
			}
		}

		return options;
	}

}
