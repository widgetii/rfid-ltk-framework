package ru.aplix.ltk.core.test.collector;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.test.reader.TestTags.randomTag;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.source.RfConnected;
import ru.aplix.ltk.core.source.RfError;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.test.reader.TestDataMessage;
import ru.aplix.ltk.core.test.reader.TestRfReaderDriver;


public class CollectorTest {

	private TestRfReaderDriver driver;
	private RfReader reader;
	private RfCollector collector;
	private CollectorConsumer collectorConsumer;

	@Before
	public void setup() {
		this.driver = new TestRfReaderDriver();
		this.reader = new RfReader(this.driver);
		this.collector = new RfCollector(this.reader.toRfSource());
		this.collectorConsumer = new CollectorConsumer();
		this.collector.subscribe(this.collectorConsumer);
	}

	@Test
	public void status() {

		final RfStatusMessage status =
				new RfConnected("test reader");

		this.driver.updateStatus(status);

		assertThat(this.collectorConsumer.nextMessage(), sameInstance(status));
		this.collectorConsumer.noMoreMessages();
	}

	@Test
	public void sameStatus() {

		final RfStatusMessage status =
				new RfConnected("test reader");

		this.driver.updateStatus(status);
		this.driver.updateStatus(status);

		assertThat(this.collectorConsumer.nextMessage(), sameInstance(status));
		this.collectorConsumer.noMoreMessages();
	}

	@Test
	public void sameError() {

		final RfStatusMessage error =
				new RfError("test reader", "error");

		this.driver.updateStatus(error);
		this.driver.updateStatus(error);

		assertThat(this.collectorConsumer.nextMessage(), sameInstance(error));
		this.collectorConsumer.noMoreMessages();
	}

	@Test
	public void reportStatusAfterError() {

		final RfStatusMessage status =
				new RfConnected("test reader");
		final RfStatusMessage error =
				new RfError("test reader", "error");

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

		final RfStatusMessage status =
				new RfConnected("test reader");

		this.driver.updateStatus(status);

		final CollectorConsumer consumer = new CollectorConsumer();

		this.collector.subscribe(consumer);

		assertThat(consumer.nextMessage(), sameInstance(status));
		consumer.noMoreMessages();
	}

	@Test
	public void lastErrorNotReported() {

		final RfStatusMessage status =
				new RfConnected("test reader");
		final RfStatusMessage error =
				new RfError("test reader", "error");

		this.driver.updateStatus(status);
		this.driver.updateStatus(error);

		final CollectorConsumer consumer = new CollectorConsumer();

		this.collector.subscribe(consumer);

		consumer.noMoreMessages();
	}

	@Test
	public void lastStatusAfterError() {

		final RfStatusMessage status =
				new RfConnected("test reader");
		final RfStatusMessage error =
				new RfError("test reader", "error");

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

		final RfStatusMessage status =
				new RfConnected("test reader");
		final RfStatusMessage error =
				new RfError("test reader", "error");

		final TagConsumer tagConsumer = new TagConsumer();

		this.collectorConsumer.handle().requestTagAppearance(tagConsumer);
		this.driver.updateStatus(status);
		this.driver.updateStatus(error);
		this.driver.sendData(new TestDataMessage(
				currentTimeMillis(),
				randomTag(),
				0,
				false));

		assertThat(tagConsumer.nextMessage(), notNullValue());

		final CollectorConsumer consumer = new CollectorConsumer();

		this.collector.subscribe(consumer);

		assertThat(consumer.nextMessage(), sameInstance(status));
		consumer.noMoreMessages();
	}

	@After
	public void stop() {
		this.collectorConsumer.handle().unsubscribe();
	}

}
