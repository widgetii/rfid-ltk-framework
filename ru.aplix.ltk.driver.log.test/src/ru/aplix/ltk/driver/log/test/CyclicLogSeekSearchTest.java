package ru.aplix.ltk.driver.log.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.driver.log.CyclicLogConfig;


public class CyclicLogSeekSearchTest {

	@Rule
	public TestReader reader = new TestReader() {
		@Override
		protected void configure(CyclicLogConfig config) {
			config.setMaxRecords(7);
		}
	};

	@Test
	public void findExisting() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(2);
		this.reader.log().write(3);

		assertThat(this.reader.seek(2), is(true));
		assertThat(this.reader.channel().position(), is(4L));
	}

	@Test
	public void findNonExisting() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(3);
		this.reader.log().write(5);

		assertThat(this.reader.seek(4), is(true));
		assertThat(this.reader.channel().position(), is(8L));
	}

	@Test
	public void dontFind() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(3);
		this.reader.log().write(5);

		assertThat(this.reader.seek(6), is(false));
	}

	@Test
	public void findSingle() throws IOException {
		this.reader.log().write(3);

		assertThat(this.reader.seek(2), is(true));
		assertThat(this.reader.channel().position(), is(0L));
	}

	@Test
	public void dontFindSingle() throws IOException {
		this.reader.log().write(3);

		assertThat(this.reader.seek(5), is(false));
	}

	@Test
	public void findInOverlapping() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(2);
		this.reader.log().write(3);
		this.reader.log().write(4);
		this.reader.log().write(5);
		this.reader.log().write(6);
		this.reader.log().write(7);
		this.reader.log().write(11);
		this.reader.log().write(12);

		assertThat(this.reader.seek(1), is(true));
		assertThat(this.reader.channel().position(), is(8L));
	}

	@Test
	public void findNewInOverlapping() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(2);
		this.reader.log().write(3);
		this.reader.log().write(4);
		this.reader.log().write(5);
		this.reader.log().write(6);
		this.reader.log().write(7);
		this.reader.log().write(13);
		this.reader.log().write(15);

		assertThat(this.reader.seek(11), is(true));
		assertThat(this.reader.channel().position(), is(0L));
	}

	@Test
	public void findNewInHeavyOverlapping() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(2);
		this.reader.log().write(3);
		this.reader.log().write(4);
		this.reader.log().write(5);
		this.reader.log().write(6);
		this.reader.log().write(7);
		this.reader.log().write(13);
		this.reader.log().write(15);
		this.reader.log().write(17);
		this.reader.log().write(19);
		this.reader.log().write(21);
		this.reader.log().write(23);

		assertThat(this.reader.seek(22), is(true));
		assertThat(this.reader.channel().position(), is(20L));
	}

	@Test
	public void findOldInOverlapping() throws IOException {
		this.reader.log().write(10);
		this.reader.log().write(20);
		this.reader.log().write(30);
		this.reader.log().write(40);
		this.reader.log().write(50);
		this.reader.log().write(60);
		this.reader.log().write(70);
		this.reader.log().write(130);
		this.reader.log().write(150);

		assertThat(this.reader.seek(55), is(true));
		assertThat(this.reader.channel().position(), is(20L));
	}

	@Test
	public void findOldInHeavilyOverlapping() throws IOException {
		this.reader.log().write(10);
		this.reader.log().write(20);
		this.reader.log().write(30);
		this.reader.log().write(40);
		this.reader.log().write(50);
		this.reader.log().write(60);
		this.reader.log().write(70);
		this.reader.log().write(110);
		this.reader.log().write(120);
		this.reader.log().write(130);
		this.reader.log().write(140);
		this.reader.log().write(150);

		assertThat(this.reader.seek(65), is(true));
		assertThat(this.reader.channel().position(), is(24L));
	}

	@Test
	public void dontFindInOverlapping() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(2);
		this.reader.log().write(3);
		this.reader.log().write(4);
		this.reader.log().write(5);
		this.reader.log().write(6);
		this.reader.log().write(7);
		this.reader.log().write(11);
		this.reader.log().write(12);

		assertThat(this.reader.seek(22), is(false));
	}

}
