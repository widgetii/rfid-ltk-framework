package ru.aplix.ltk.collector.log.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.collector.log.CyclicLog;
import ru.aplix.ltk.collector.log.CyclicLogConfig;


public class TruncateCyclicLogTest {

	private File file;
	private CyclicLogConfig config;
	private CyclicLog log;

	@Before
	public void createLog() throws IOException {
		this.file = File.createTempFile("cyclic-log", "bin");
		this.config =
				new CyclicLogConfig(this.file.toPath(), 4).setMaxRecords(5);
		this.log = new CyclicLog(this.config);
	}

	@Test
	public void truncateLog() throws IOException {
		write(1);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(2));
		write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(8L));
		assertThat(readValue(), is(1));
		assertThat(readValue(), is(11));
	}

	@Test
	public void dontTruncateLog() throws IOException {
		write(1);
		write(2);
		write(3);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(3));
		write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(12L));
		assertThat(readValue(), is(11));
		assertThat(readValue(), is(2));
		assertThat(readValue(), is(3));
	}

	@Test
	public void truncateFullLog() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		write(5);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(2));
		write(11);
		write(12);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(8L));
		assertThat(readValue(), is(11));
		assertThat(readValue(), is(12));
	}

	@Test
	public void dontTruncateFullLog() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		write(5);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(2));
		write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(20L));
		assertThat(readValue(), is(11));
		assertThat(readValue(), is(2));
		assertThat(readValue(), is(3));
		assertThat(readValue(), is(4));
		assertThat(readValue(), is(5));
	}

	@Test
	public void truncateOverflownLog() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		write(5);
		write(6);
		write(7);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(3));
		write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(12L));
		assertThat(readValue(), is(6));
		assertThat(readValue(), is(7));
		assertThat(readValue(), is(11));
	}

	@Test
	public void dontTruncateOverflownLog() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		write(5);
		write(6);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(3));
		write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(20L));
		assertThat(readValue(), is(6));
		assertThat(readValue(), is(11));
		assertThat(readValue(), is(3));
		assertThat(readValue(), is(4));
		assertThat(readValue(), is(5));
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

	private int readValue() throws IOException {

		final ByteBuffer in = this.log.record();

		in.clear();
		this.log.channel().read(in);
		in.clear();

		return in.getInt();
	}

}
