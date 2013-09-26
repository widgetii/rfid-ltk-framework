package ru.aplix.ltk.collector.log.impl;

import static ru.aplix.ltk.collector.log.RfLogConstants.RF_LOG_PREFIX;
import static ru.aplix.ltk.collector.log.RfLogConstants.RF_LOG_SIZE;
import static ru.aplix.ltk.osgi.OSGiUtils.bundleParameters;

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
	private long lastId;

	TagLog(BundleContext context, RfProvider<?> provider) throws IOException {
		this(config(context, provider));
	}

	private TagLog(CyclicLogConfig config) throws IOException {
		super(config);
		this.idPath = getPath().resolveSibling(getPath().getFileName() + ".id");
		this.idRecord = ByteBuffer.allocate(8);
		this.idChannel = FileChannel.open(this.idPath, config.openOptions());
		initId();
	}

	public RfTagAppearanceRecord write(
			RfTagAppearanceMessage message)
	throws IOException {

		final RfTagAppearanceRecord record =
				new RfTagAppearanceRecord(++this.lastId, message);

		record.write(record());
		write();
		storeId(record);

		return record;
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
		this.idChannel.write(this.idRecord);
	}

	private static CyclicLogConfig config(
			BundleContext context,
			RfProvider<?> provider) {

		final Path path =
				context.getDataFile(provider.getId() + ".tags").toPath();
		final CyclicLogConfig config = new CyclicLogConfig(path, 32);
		final Parameters params = bundleParameters(context).sub(RF_LOG_PREFIX);

		config.setMaxRecords(params.valueOf(RF_LOG_SIZE));

		return config;
	}

}
