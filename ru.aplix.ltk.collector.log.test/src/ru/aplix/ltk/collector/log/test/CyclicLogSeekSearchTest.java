package ru.aplix.ltk.collector.log.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.collector.log.*;


public class CyclicLogSeekSearchTest {

	private CyclicLog log;
	private CyclicLogReader reader;

	@Before
	public void createLog() throws IOException {

		final File file = File.createTempFile("cyclic-log", "bin");

		this.log = new CyclicLog(
				new CyclicLogConfig(file.toPath(), 4)
				.setMaxRecords(7));
		this.reader = this.log.read();
	}

	@Test
	public void findExisting() throws IOException {
		write(1);
		write(2);
		write(3);

		assertThat(seek(2), is(true));
		assertThat(this.reader.channel().position(), is(4L));
	}

	@Test
	public void findNonExisting() throws IOException {
		write(1);
		write(3);
		write(5);

		assertThat(seek(4), is(true));
		assertThat(this.reader.channel().position(), is(8L));
	}

	@Test
	public void dontFind() throws IOException {
		write(1);
		write(3);
		write(5);

		assertThat(seek(6), is(false));
	}

	@Test
	public void findSingle() throws IOException {
		write(3);

		assertThat(seek(2), is(true));
		assertThat(this.reader.channel().position(), is(0L));
	}

	@Test
	public void dontFindSingle() throws IOException {
		write(3);

		assertThat(seek(5), is(false));
	}

	@Test
	public void findInOverlapping() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		write(5);
		write(6);
		write(7);
		write(11);
		write(12);

		assertThat(seek(1), is(true));
		assertThat(this.reader.channel().position(), is(8L));
	}

	@Test
	public void findNewInOverlapping() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		write(5);
		write(6);
		write(7);
		write(13);
		write(15);

		assertThat(seek(11), is(true));
		assertThat(this.reader.channel().position(), is(0L));
	}

	@Test
	public void findNewInHeavyOverlapping() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		write(5);
		write(6);
		write(7);
		write(13);
		write(15);
		write(17);
		write(19);
		write(21);
		write(23);

		assertThat(seek(22), is(true));
		assertThat(this.reader.channel().position(), is(20L));
	}

	@Test
	public void findOldInOverlapping() throws IOException {
		write(10);
		write(20);
		write(30);
		write(40);
		write(50);
		write(60);
		write(70);
		write(130);
		write(150);

		assertThat(seek(55), is(true));
		assertThat(this.reader.channel().position(), is(20L));
	}

	@Test
	public void findOldInHeavilyOverlapping() throws IOException {
		write(10);
		write(20);
		write(30);
		write(40);
		write(50);
		write(60);
		write(70);
		write(110);
		write(120);
		write(130);
		write(140);
		write(150);

		assertThat(seek(65), is(true));
		assertThat(this.reader.channel().position(), is(24L));
	}

	@Test
	public void dontFindInOverlapping() throws IOException {
		write(1);
		write(2);
		write(3);
		write(4);
		write(5);
		write(6);
		write(7);
		write(11);
		write(12);

		assertThat(seek(22), is(false));
	}

	@After
	public void deleteLog() throws IOException {
		this.reader.close();
		this.log.delete();
	}

	private void write(int value) throws IOException {

		final ByteBuffer out = this.log.record();

		out.clear();
		out.putInt(value);

		this.log.write();
	}

	private boolean seek(int toFind) throws IOException {
		return this.reader.seek(new TestLogFilter(toFind));
	}

	static final class TestLogFilter implements CyclicLogFilter {

		private final int toFind;

		TestLogFilter(int toFind) {
			this.toFind = toFind;
		}

		@Override
		public int filterRecord(CyclicLogReader reader) throws IOException {

			final int value = reader.read().getInt();

			return value - this.toFind;
		}

	}

}
