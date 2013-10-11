package ru.aplix.ltk.driver.log.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.driver.log.CyclicLogConfig;


public class TruncateCyclicLogTest {

	@Rule
	public TestLog log = new TestLog() {
		@Override
		protected void configure(CyclicLogConfig config) {
			config.setMaxRecords(5);
		}
	};

	@Test
	public void truncateLog() throws IOException {
		this.log.write(1);
		this.log.reopen(this.log.config().setMaxRecords(2));
		this.log.write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(8L));
		assertThat(this.log.readValue(), is(1));
		assertThat(this.log.readValue(), is(11));
	}

	@Test
	public void dontTruncateLog() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);
		this.log.reopen(this.log.config().setMaxRecords(3));
		this.log.write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(12L));
		assertThat(this.log.readValue(), is(11));
		assertThat(this.log.readValue(), is(2));
		assertThat(this.log.readValue(), is(3));
	}

	@Test
	public void truncateFullLog() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);
		this.log.write(4);
		this.log.write(5);
		this.log.reopen(this.log.config().setMaxRecords(2));
		this.log.write(11);
		this.log.write(12);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(8L));
		assertThat(this.log.readValue(), is(11));
		assertThat(this.log.readValue(), is(12));
	}

	@Test
	public void dontTruncateFullLog() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);
		this.log.write(4);
		this.log.write(5);
		this.log.reopen(this.log.config().setMaxRecords(2));
		this.log.write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(20L));
		assertThat(this.log.readValue(), is(11));
		assertThat(this.log.readValue(), is(2));
		assertThat(this.log.readValue(), is(3));
		assertThat(this.log.readValue(), is(4));
		assertThat(this.log.readValue(), is(5));
	}

	@Test
	public void truncateOverflownLog() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);
		this.log.write(4);
		this.log.write(5);
		this.log.write(6);
		this.log.write(7);
		this.log.reopen(this.log.config().setMaxRecords(3));
		this.log.write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(12L));
		assertThat(this.log.readValue(), is(6));
		assertThat(this.log.readValue(), is(7));
		assertThat(this.log.readValue(), is(11));
	}

	@Test
	public void dontTruncateOverflownLog() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);
		this.log.write(4);
		this.log.write(5);
		this.log.write(6);
		this.log.reopen(this.log.config().setMaxRecords(3));
		this.log.write(11);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(20L));
		assertThat(this.log.readValue(), is(6));
		assertThat(this.log.readValue(), is(11));
		assertThat(this.log.readValue(), is(3));
		assertThat(this.log.readValue(), is(4));
		assertThat(this.log.readValue(), is(5));
	}

}
