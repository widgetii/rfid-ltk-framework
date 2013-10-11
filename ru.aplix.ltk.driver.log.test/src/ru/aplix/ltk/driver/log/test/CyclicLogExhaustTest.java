package ru.aplix.ltk.driver.log.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.driver.log.CyclicLogConfig;


public class CyclicLogExhaustTest {

	@Rule
	public TestReader reader = new TestReader() {
		@Override
		protected void configure(CyclicLogConfig config) {
			config.setMaxRecords(4);
		}
	};

	@Test
	public void emptyLog() throws IOException {
		assertThat(this.reader.seek(0), is(false));
		assertThat(this.reader.isExhausted(), is(true));
	}

	@Test
	public void notFound() throws IOException {
		this.reader.log().write(1);

		assertThat(this.reader.isExhausted(), is(false));
		assertThat(this.reader.seek(2), is(false));
		assertThat(this.reader.isExhausted(), is(true));
	}

	@Test
	public void found() throws IOException {
		this.reader.log().write(1);

		assertThat(this.reader.isExhausted(), is(false));
		assertThat(this.reader.seek(1), is(true));
		assertThat(this.reader.isExhausted(), is(false));
	}

	@Test
	public void pull() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(2);
		this.reader.log().write(3);

		assertThat(this.reader.isExhausted(), is(false));
		assertThat(this.reader.seek(2), is(true));
		assertThat(this.reader.isExhausted(), is(false));
		assertThat(this.reader.pull(), is(2));
		assertThat(this.reader.isExhausted(), is(false));
		assertThat(this.reader.pull(), is(3));
		assertThat(this.reader.isExhausted(), is(true));
	}

	@Test
	public void writeAfterFound() throws IOException {
		this.reader.log().write(1);
		this.reader.log().write(2);

		assertThat(this.reader.isExhausted(), is(false));
		assertThat(this.reader.seek(2), is(true));
		assertThat(this.reader.isExhausted(), is(false));

		this.reader.log().write(3);

		assertThat(this.reader.pull(), is(2));
		assertThat(this.reader.isExhausted(), is(false));
		assertThat(this.reader.pull(), is(3));
		assertThat(this.reader.isExhausted(), is(true));
	}

}
