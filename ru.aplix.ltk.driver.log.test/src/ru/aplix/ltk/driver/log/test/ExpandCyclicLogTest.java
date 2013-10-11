package ru.aplix.ltk.driver.log.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.driver.log.CyclicLogConfig;


public class ExpandCyclicLogTest {

	@Rule
	public TestLog log = new TestLog() {
		@Override
		protected void configure(CyclicLogConfig config) {
			config.setMaxRecords(3);
		}
	};

	@Test
	public void expandLog() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.reopen(this.log.config().setMaxRecords(5));
		this.log.write(11);
		this.log.write(12);
		this.log.write(13);
		this.log.write(14);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(20L));
		assertThat(this.log.readValue(), is(14));
		assertThat(this.log.readValue(), is(2));
		assertThat(this.log.readValue(), is(11));
		assertThat(this.log.readValue(), is(12));
		assertThat(this.log.readValue(), is(13));
	}

	@Test
	public void expandFullLog() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);
		this.log.reopen(this.log.config().setMaxRecords(5));
		this.log.write(14);
		this.log.write(15);
		this.log.write(16);
		this.log.write(17);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(16L));
		assertThat(this.log.readValue(), is(14));
		assertThat(this.log.readValue(), is(15));
		assertThat(this.log.readValue(), is(16));
		assertThat(this.log.readValue(), is(17));
	}

	@Test
	public void expandOverflownLog() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);
		this.log.write(4);
		this.log.reopen(this.log.config().setMaxRecords(5));
		this.log.write(11);
		this.log.write(12);
		this.log.write(13);
		this.log.write(14);

		this.log.channel().position(0);

		assertThat(this.log.size(), is(20L));
		assertThat(this.log.readValue(), is(4));
		assertThat(this.log.readValue(), is(11));
		assertThat(this.log.readValue(), is(12));
		assertThat(this.log.readValue(), is(13));
		assertThat(this.log.readValue(), is(14));
	}

}
