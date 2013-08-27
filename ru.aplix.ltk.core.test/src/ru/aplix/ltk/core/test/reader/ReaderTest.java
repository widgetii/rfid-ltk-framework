package ru.aplix.ltk.core.test.reader;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.core.reader.*;


public class ReaderTest {

	private TestRfReaderDriver driver;
	private RfReader reader;
	private ReaderConsumer readerConsumer;

	@Before
	public void setup() {
		this.driver = new TestRfReaderDriver();
		this.reader = new RfReader(this.driver);
		this.readerConsumer = new ReaderConsumer();
		this.reader.subscribe(this.readerConsumer);
	}

	@Test
	public void status() {

		final RfReaderStatusMessage status =
				new RfReaderConnected("test reader");

		this.driver.updateStatus(status);

		assertThat(this.readerConsumer.nextMessage(), sameInstance(status));
		this.readerConsumer.noMoreMessages();
	}

	@Test
	public void data() {

		final ReadConsumer consumer = new ReadConsumer();

		this.readerConsumer.handle().requestData(consumer);

		final RfDataMessage data = new TestDataMessage(
				System.currentTimeMillis(),
				new TestTag(),
				0,
				false);

		this.driver.sendData(data);

		assertThat(consumer.nextMessage(), sameInstance(data));
		this.readerConsumer.noMoreMessages();
	}

}
