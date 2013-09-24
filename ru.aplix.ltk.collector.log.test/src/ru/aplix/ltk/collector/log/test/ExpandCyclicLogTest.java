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


public class ExpandCyclicLogTest {

	private File file;
	private CyclicLogConfig config;
	private CyclicLog log;

	@Before
	public void createLog() throws IOException {
		this.file = File.createTempFile("cyclic-log", "bin");
		this.config =
				new CyclicLogConfig(this.file.toPath(), 4).setMaxRecords(3);
		this.log = new CyclicLog(this.config);
	}

	@Test
	public void expandLog() throws IOException {
		write(1);
		write(2);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(5));
		write(11);
		write(12);
		write(13);
		write(14);

		this.log.logChannel().position(0);

		assertThat(this.log.size(), is(20L));
		assertThat(readValue(), is(14));
		assertThat(readValue(), is(2));
		assertThat(readValue(), is(11));
		assertThat(readValue(), is(12));
		assertThat(readValue(), is(13));
	}

	@Test
	public void expandFullLog() throws IOException {
		write(1);
		write(2);
		write(3);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(5));
		write(14);
		write(15);
		write(16);
		write(17);

		this.log.logChannel().position(0);

		assertThat(this.log.size(), is(16L));
		assertThat(readValue(), is(14));
		assertThat(readValue(), is(15));
		assertThat(readValue(), is(16));
		assertThat(readValue(), is(17));
	}

	@Test
	public void expandOverflownLog() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		this.log.close();
		this.log = new CyclicLog(this.config.setMaxRecords(5));
		write(11);
		write(12);
		write(13);
		write(14);

		this.log.logChannel().position(0);

		assertThat(this.log.size(), is(20L));
		assertThat(readValue(), is(4));
		assertThat(readValue(), is(11));
		assertThat(readValue(), is(12));
		assertThat(readValue(), is(13));
		assertThat(readValue(), is(14));
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
		this.log.logChannel().read(in);
		in.clear();

		return in.getInt();
	}

}
