package ru.aplix.ltk.driver.log.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.driver.log.CyclicLogConfig;


public class CyclicLogTest {

	@Rule
	public TestLog log = new TestLog() {
		@Override
		protected void configure(CyclicLogConfig config) {
			config.setMaxRecords(3);
		}
	};

	@Test
	public void open() throws IOException {
		assertThat(this.log.log().getRecordSize(), is(4));
		assertThat(this.log.log().getMaxSize(), is(12L));
		assertThat(this.log.channel().position(), is(0L));
		assertThat(this.log.channel().size(), is(0L));
		assertThat(this.log.positionChannel().size(), is(0L));
	}

	@Test
	public void write() throws IOException {
		this.log.write(1);

		assertThat(this.log.channel().position(), is(4L));
		assertThat(this.log.channel().size(), is(4L));
		assertThat(this.log.positionChannel().size(), is(8L));
		assertThat(this.log.readPosition(), is(4L));

		this.log.channel().position(0);

		assertThat(this.log.readValue(), is(1));
	}

	@Test
	public void writeMore() throws IOException {
		this.log.write(1);
		this.log.write(2);

		assertThat(this.log.channel().position(), is(8L));
		assertThat(this.log.channel().size(), is(8L));
		assertThat(this.log.positionChannel().size(), is(8L));
		assertThat(this.log.readPosition(), is(8L));

		this.log.channel().position(0);

		assertThat(this.log.readValue(), is(1));
		assertThat(this.log.readValue(), is(2));
	}

	@Test
	public void fulfill() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);

		assertThat(this.log.channel().position(), is(0L));
		assertThat(this.log.channel().size(), is(12L));
		assertThat(this.log.positionChannel().size(), is(8L));
		assertThat(this.log.readPosition(), is(0L));

		this.log.channel().position(0);

		assertThat(this.log.readValue(), is(1));
		assertThat(this.log.readValue(), is(2));
		assertThat(this.log.readValue(), is(3));
	}

	@Test
	public void cycle() throws IOException {
		this.log.write(1);
		this.log.write(2);
		this.log.write(3);
		this.log.write(4);

		assertThat(this.log.channel().position(), is(4L));
		assertThat(this.log.channel().size(), is(12L));
		assertThat(this.log.positionChannel().size(), is(8L));
		assertThat(this.log.readPosition(), is(4L));

		this.log.channel().position(0);

		assertThat(this.log.readValue(), is(4));
		assertThat(this.log.readValue(), is(2));
		assertThat(this.log.readValue(), is(3));
	}

}
