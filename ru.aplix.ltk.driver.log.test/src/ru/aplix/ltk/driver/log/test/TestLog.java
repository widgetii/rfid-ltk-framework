package ru.aplix.ltk.driver.log.test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;

import org.junit.rules.ExternalResource;

import ru.aplix.ltk.driver.log.CyclicLog;
import ru.aplix.ltk.driver.log.CyclicLogConfig;


public abstract class TestLog extends ExternalResource {

	private CyclicLog log;
	private CyclicLogConfig config;

	public final CyclicLog log() {
		return this.log;
	}

	public final CyclicLogConfig config() {
		return this.config;
	}

	public final FileChannel channel() {
		return log().channel();
	}

	public final long size() throws IOException {
		return log().size();
	}

	public final FileChannel positionChannel() {
		return log().positionChannel();
	}

	public CyclicLog reopen(CyclicLogConfig config) throws IOException {
		log().close();
		this.config = config;
		return this.log = new CyclicLog(this.config);
	}

	public void write(int value) throws IOException {

		final ByteBuffer out = this.log.record();

		out.clear();
		out.putInt(value);

		this.log.write();
	}

	public long readPosition() throws IOException {

		final ByteBuffer in = ByteBuffer.allocate(8);
		final LongBuffer data = in.asLongBuffer();

		this.log.positionChannel().position(0).read(in);

		return data.get();
	}

	public int readValue() throws IOException {

		final ByteBuffer in = log().record();

		in.clear();
		this.log.channel().read(in);
		in.clear();

		return in.getInt();
	}

	@Override
	protected void before() throws Throwable {

		final File file = File.createTempFile("cyclic-log", "bin");

		this.config = new CyclicLogConfig(file.toPath(), 4);
		configure(this.config);

		this.log = new CyclicLog(this.config);
	}

	protected abstract void configure(CyclicLogConfig config);

	@Override
	protected void after() {
		try {
			this.log.delete();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
