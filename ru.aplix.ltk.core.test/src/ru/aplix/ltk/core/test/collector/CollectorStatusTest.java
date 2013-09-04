package ru.aplix.ltk.core.test.collector;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.source.RfConnected;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.test.reader.TestRfReaderDriver;


public class CollectorStatusTest {

	private TestRfReaderDriver driver;
	private RfReader reader;
	private RfCollector collector;
	private RfStatusMessage firstStatus;

	@Before
	public void setup() {
		this.firstStatus = new RfConnected("test reader");
		this.driver = new TestRfReaderDriver() {
			@Override
			public void startRfReader() {
				updateStatus(CollectorStatusTest.this.firstStatus);
			}
		};
		this.reader = new RfReader(this.driver);
		this.collector = new RfCollector(this.reader);
	}

	@Test
	public void veryFirstSubscriberStatus() {

		final CollectorConsumer consumer = new CollectorConsumer();

		this.collector.subscribe(consumer);
		assertThat(consumer.nextMessage(), sameInstance(this.firstStatus));
		consumer.noMoreMessages();
	}

}
