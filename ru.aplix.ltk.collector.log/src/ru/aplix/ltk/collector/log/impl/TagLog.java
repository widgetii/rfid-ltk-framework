package ru.aplix.ltk.collector.log.impl;

import static ru.aplix.ltk.collector.log.RfLogConstants.*;
import static ru.aplix.ltk.osgi.OSGiUtils.bundleParameters;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import org.osgi.framework.BundleContext;

import ru.aplix.ltk.collector.log.CyclicLog;
import ru.aplix.ltk.collector.log.CyclicLogConfig;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.util.Parameters;


class TagLog extends CyclicLog {

	private final Path idPath;
	private final ByteBuffer idRecord;
	private final FileChannel idChannel;
	private final boolean logAppearance;
	private final boolean logDisappearance;
	private long lastId;

	TagLog(BundleContext context, RfProvider<?> provider) throws IOException {
		this(context, provider, bundleParameters(context).sub(RF_LOG_PREFIX));
	}

	private TagLog(
			BundleContext context,
			RfProvider<?> provider,
			Parameters params)
	throws IOException {
		this(
				config(context, provider, params),
				params.valueOf(RF_LOG_APPEARANCE),
				params.valueOf(RF_LOG_DISAPPEARANCE));
	}

	private TagLog(
			CyclicLogConfig config,
			boolean logAppearance,
			boolean logDisappearance)
	throws IOException {
		super(config);
		this.logAppearance = logAppearance;
		this.logDisappearance = logDisappearance;
		this.idPath = getPath().resolveSibling(getPath().getFileName() + ".id");
		this.idRecord = ByteBuffer.allocate(8);
		this.idChannel = FileChannel.open(this.idPath, config.openOptions());
		initId();
	}

	public RfTagAppearanceMessage log(
			RfTagAppearanceMessage message)
	throws IOException {
		switch (message.getAppearance()) {
		case RF_TAG_APPEARED:
			if (!this.logAppearance) {
				return message;
			}
			break;
		case RF_TAG_DISAPPEARED:
			if (!this.logDisappearance) {
				return message;
			}
			break;
		}

		synchronized (this) {

			final RfTagAppearanceRecord record =
					new RfTagAppearanceRecord(++this.lastId, message);

			record.write(record());
			write();
			storeId(record);

			return record;
		}
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			this.idChannel.close();
		}
	}

	@Override
	public void delete() throws IOException {
		super.delete();
		this.idPath.toFile().delete();
	}

	private void initId() throws IOException {
		this.idChannel.read(this.idRecord);
		if (this.idRecord.hasRemaining()) {
			return;
		}
		this.idRecord.clear();
		this.lastId = this.idRecord.getLong();
	}

	private void storeId(RfTagAppearanceRecord record) throws IOException {
		this.idRecord.clear();
		this.idRecord.putLong(record.getEventId());
		this.idRecord.clear();
		this.idChannel.position(0);
		this.idChannel.write(this.idRecord);
	}

	private static CyclicLogConfig config(
			BundleContext context,
			RfProvider<?> provider,
			Parameters params) {

		final Path path = logPath(context, provider, params);
		final CyclicLogConfig config = new CyclicLogConfig(path, 32);

		config.setMaxRecords(params.valueOf(RF_LOG_SIZE));
		config.setSync(params.valueOf(RF_LOG_FSYNC));
		config.setSyncMeta(params.valueOf(RF_LOG_FSYNC_META));

		return config;
	}

	private static Path logPath(
			BundleContext context,
			RfProvider<?> provider,
			Parameters params) {

		final String dir = params.valueOf(RF_LOG_DIR, "").trim();
		final String fileName = provider.getId() + ".tags";

		if (dir.isEmpty()) {
			return context.getDataFile(fileName).toPath();
		}

		final File dirFile = new File(dir);

		if (!dirFile.isDirectory() && !dirFile.mkdirs()) {
			throw new IllegalStateException(
					"Failed to create collector log directory: " + dirFile);
		}

		return dirFile.toPath().resolve(fileName);
	}

}
