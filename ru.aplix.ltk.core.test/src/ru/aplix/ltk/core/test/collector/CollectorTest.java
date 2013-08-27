package ru.aplix.ltk.core.test.collector;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.reader.*;
import ru.aplix.ltk.core.test.reader.TestDataMessage;
import ru.aplix.ltk.core.test.reader.TestRfReaderDriver;
import ru.aplix.ltk.core.test.reader.TestTag;


public class CollectorTest {

	private TestRfReaderDriver driver;
	private RfReader reader;
	private RfCollector collector;
	private CollectorConsumer collectorConsumer;

	@Before
	public void setup() {
		this.driver = new TestRfReaderDriver();
		this.reader = new RfReader(this.driver);
		this.collector = new RfCollector(this.reader);
		this.collectorConsumer = new CollectorConsumer();
		this.collector.subscribe(this.collectorConsumer);
	}

	@Test
	public void status() {

		final RfReaderStatusMessage status =
				new RfReaderConnected("test reader");

		this.driver.updateStatus(status);

		assertThat(this.collectorConsumer.nextMessage(), sameInstance(status));
		this.collectorConsumer.noMoreMessages();
	}

	@Test
	public void sameStatus() {

		final RfReaderStatusMessage status =
				new RfReaderConnected("test reader");

		this.driver.updateStatus(status);
		this.driver.updateStatus(status);

		assertThat(this.collectorConsumer.nextMessage(), sameInstance(status));
		this.collectorConsumer.noMoreMessages();
	}

	@Test
	public void sameError() {

		final RfReaderStatusMessage error =
				new RfReaderError("test reader", "error");

		this.driver.updateStatus(error);
		this.driver.updateStatus(error);

		assertThat(this.collectorConsumer.nextMessage(), sameInstance(error));
		this.collectorConsumer.noMoreMessages();
	}

	@Test
	public void reportStatusAfterError() {

		final RfReaderStatusMessage status =
				new RfReaderConnected("test reader");
		final RfReaderStatusMessage error =
				new RfReaderError("test reader", "error");

		this.driver.updateStatus(status);
		this.driver.updateStatus(error);
		this.driver.updateStatus(status);

		assertThat(this.collectorConsumer.nextMessage(), sameInstance(status));
		assertThat(this.collectorConsumer.nextMessage(), sameInstance(error));
		assertThat(this.collectorConsumer.nextMessage(), sameInstance(status));
		this.collectorConsumer.noMoreMessages();
	}

	@Test
	public void lastStatus() {

		final RfReaderStatusMessage status =
				new RfReaderConnected("test reader");

		this.driver.updateStatus(status);

		final CollectorConsumer consumer = new CollectorConsumer();

		this.collector.subscribe(consumer);

		assertThat(consumer.nextMessage(), sameInstance(status));
		consumer.noMoreMessages();
	}

	@Test
	public void lastError() {

		final RfReaderStatusMessage status =
				new RfReaderConnected("test reader");
		final RfReaderStatusMessage error =
				new RfReaderError("test reader", "error");

		this.driver.updateStatus(status);
		this.driver.updateStatus(error);

		final CollectorConsumer consumer = new CollectorConsumer();

		this.collector.subscribe(consumer);

		assertThat(consumer.nextMessage(), sameInstance(error));
		consumer.noMoreMessages();
	}

	@Test
	public void lastStatusAfterError() {

		final RfReaderStatusMessage status =
				new RfReaderConnected("test reader");
		final RfReaderStatusMessage error =
				new RfReaderError("test reader", "error");

		this.driver.updateStatus(status);
		this.driver.updateStatus(error);
		this.driver.updateStatus(status);

		final CollectorConsumer consumer = new CollectorConsumer();

		this.collector.subscribe(consumer);

		assertThat(consumer.nextMessage(), sameInstance(status));
		consumer.noMoreMessages();
	}

	@Test
	public void lastStatusAfterData() {

		final RfReaderStatusMessage status =
				new RfReaderConnected("test reader");
		final RfReaderStatusMessage error =
				new RfReaderError("test reader", "error");

		final TagConsumer tagConsumer = new TagConsumer();

		this.collectorConsumer.handle().requestTagAppearance(tagConsumer);
		this.driver.updateStatus(status);
		this.driver.updateStatus(error);
		this.driver.sendData(new TestDataMessage(
				currentTimeMillis(),
				new TestTag(),
				0,
				false));

		assertThat(tagConsumer.nextMessage(), notNullValue());

		final CollectorConsumer consumer = new CollectorConsumer();

		this.collector.subscribe(consumer);

		assertThat(consumer.nextMessage(), sameInstance(status));
		consumer.noMoreMessages();
	}

}
