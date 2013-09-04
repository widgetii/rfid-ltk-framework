package ru.aplix.ltk.core.test.collector;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.core.collector.DefaultRfTracker;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.test.reader.TestDataMessage;
import ru.aplix.ltk.core.test.reader.TestRfReaderDriver;
import ru.aplix.ltk.core.test.reader.TestTag;


public class InvalidationTimeoutTest {

	private TestRfReaderDriver driver;
	private RfReader reader;
	private RfCollector collector;
	private CollectorConsumer collectorConsumer;
	private TagConsumer tagConsumer;

	@Before
	public void setup() {
		this.driver = new TestRfReaderDriver();
		this.reader = new RfReader(this.driver);

		final DefaultRfTracker tracker = new DefaultRfTracker();

		tracker.setTransactionTimeout(200);
		tracker.setInvalidationTimeout(500);

		this.collector = new RfCollector(this.reader, tracker);
		this.collectorConsumer = new CollectorConsumer();
		this.collector.subscribe(this.collectorConsumer);
		this.tagConsumer = new TagConsumer();
		this.collectorConsumer.handle().requestTagAppearance(this.tagConsumer);
	}

	@Test
	public void tagDisappeared() {

		final RfTag tag = new TestTag();
		final TestDataMessage data = new TestDataMessage(
				currentTimeMillis(),
				tag,
				0,
				false);

		this.driver.sendData(data);

		final RfTagAppearanceMessage message1 = this.tagConsumer.nextMessage();

		assertThat(message1.getAppearance(), is(RF_TAG_APPEARED));
		assertThat(message1.getRfTag(), sameInstance(tag));

		final RfTagAppearanceMessage message2 = this.tagConsumer.nextMessage();

		assertThat(message2.getAppearance(), is(RF_TAG_DISAPPEARED));
		assertThat(message2.getRfTag(), sameInstance(tag));

		this.tagConsumer.noMoreMessages();
	}

	@After
	public void stop() {
		this.collectorConsumer.handle().unsubscribe();
	}

}
