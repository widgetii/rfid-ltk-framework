package ru.aplix.ltk.collector.log.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.collector.log.CyclicLog;
import ru.aplix.ltk.collector.log.CyclicLogConfig;


public class CyclicLogTest {

	private CyclicLog log;

	@Before
	public void createLog() throws IOException {

		final File file = File.createTempFile("cyclic-log", "bin");

		this.log = new CyclicLog(
				new CyclicLogConfig(file.toPath(), 4)
				.setMaxRecords(3));
	}

	@Test
	public void open() throws IOException {
		assertThat(this.log.getRecordSize(), is(4));
		assertThat(this.log.getMaxSize(), is(12L));
		assertThat(this.log.channel().position(), is(0L));
		assertThat(this.log.channel().size(), is(0L));
		assertThat(this.log.positionChannel().size(), is(0L));
	}

	@Test
	public void write() throws IOException {
		write(1);

		assertThat(this.log.channel().position(), is(4L));
		assertThat(this.log.channel().size(), is(4L));
		assertThat(this.log.positionChannel().size(), is(8L));
		assertThat(readPosition(), is(4L));

		this.log.channel().position(0);

		assertThat(readValue(), is(1));
	}

	@Test
	public void writeMore() throws IOException {
		write(1);
		write(2);
		assertThat(this.log.channel().position(), is(8L));
		assertThat(this.log.channel().size(), is(8L));
		assertThat(this.log.positionChannel().size(), is(8L));
		assertThat(readPosition(), is(8L));

		this.log.channel().position(0);

		assertThat(readValue(), is(1));
		assertThat(readValue(), is(2));
	}

	@Test
	public void fulfill() throws IOException {
		write(1);
		write(2);
		write(3);
		assertThat(this.log.channel().position(), is(0L));
		assertThat(this.log.channel().size(), is(12L));
		assertThat(this.log.positionChannel().size(), is(8L));
		assertThat(readPosition(), is(0L));

		this.log.channel().position(0);

		assertThat(readValue(), is(1));
		assertThat(readValue(), is(2));
		assertThat(readValue(), is(3));
	}

	@Test
	public void cycle() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		assertThat(this.log.channel().position(), is(4L));
		assertThat(this.log.channel().size(), is(12L));
		assertThat(this.log.positionChannel().size(), is(8L));
		assertThat(readPosition(), is(4L));

		this.log.channel().position(0);

		assertThat(readValue(), is(4));
		assertThat(readValue(), is(2));
		assertThat(readValue(), is(3));
	}

	@After
	public void deleteLog() throws IOException {
		this.log.delete();
	}

	private void write(int value) throws IOException {

		final ByteBuffer out = this.log.record();

		out.clear();
		out.putInt(value);

		this.log.write();
	}

	private long readPosition() throws IOException {

		final ByteBuffer in = ByteBuffer.allocate(8);
		final LongBuffer data = in.asLongBuffer();

		this.log.positionChannel().position(0).read(in);

		return data.get();
	}

	private int readValue() throws IOException {

		final ByteBuffer in = this.log.record();

		in.clear();
		this.log.channel().read(in);
		in.clear();

		return in.getInt();
	}

}
