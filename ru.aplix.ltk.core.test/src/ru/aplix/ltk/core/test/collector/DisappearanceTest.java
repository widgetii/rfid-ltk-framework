package ru.aplix.ltk.core.test.collector;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.collector.DefaultRfTracker.DEFAULT_INVALIDATION_TIMEOUT;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.test.reader.TestDataMessage;
import ru.aplix.ltk.core.test.reader.TestRfReaderDriver;
import ru.aplix.ltk.core.test.reader.TestTag;


public class DisappearanceTest {

	private TestRfReaderDriver driver;
	private RfReader reader;
	private RfCollector collector;
	private CollectorConsumer collectorConsumer;
	private TagConsumer tagConsumer;

	@Before
	public void setup() {
		this.driver = new TestRfReaderDriver();
		this.reader = new RfReader(this.driver);
		this.collector = new RfCollector(this.reader);
		this.collectorConsumer = new CollectorConsumer();
		this.collector.subscribe(this.collectorConsumer);
		this.tagConsumer = new TagConsumer();
		this.collectorConsumer.handle().requestTagAppearance(this.tagConsumer);
	}

	@Test
	public void tagDisappeared() {

		final RfTag tag = new TestTag();
		final TestDataMessage data1 = new TestDataMessage(
				currentTimeMillis(),
				tag,
				0,
				false);
		final TestDataMessage data2 = new TestDataMessage(
				data1.getTimestamp() + 1,
				tag,
				0,
				true);
		final TestDataMessage data3 = new TestDataMessage(
				data2.getTimestamp() + DEFAULT_INVALIDATION_TIMEOUT,
				null,
				0,
				false);

		this.driver.sendData(data1);
		this.driver.sendData(data2);// end transaction
		this.driver.sendData(data3);

		final RfTagAppearanceMessage message1 = this.tagConsumer.nextMessage();

		assertThat(message1.getAppearance(), is(RF_TAG_APPEARED));
		assertThat(message1.getRfTag(), sameInstance(tag));

		final RfTagAppearanceMessage message2 = this.tagConsumer.nextMessage();

		assertThat(message2.getAppearance(), is(RF_TAG_DISAPPEARED));
		assertThat(message2.getRfTag(), sameInstance(tag));

		this.tagConsumer.noMoreMessages();
	}

	@Test
	public void tagNotDisappeared() {

		final RfTag tag = new TestTag();
		final TestDataMessage data1 = new TestDataMessage(
				currentTimeMillis(),
				tag,
				0,
				false);
		final TestDataMessage data2 = new TestDataMessage(
				data1.getTimestamp() + 1,
				tag,
				0,
				true);
		final TestDataMessage data3 = new TestDataMessage(
				data2.getTimestamp() + DEFAULT_INVALIDATION_TIMEOUT - 1,
				null,
				0,
				false);

		this.driver.sendData(data1);
		this.driver.sendData(data2);// end transaction
		this.driver.sendData(data3);

		final RfTagAppearanceMessage message1 = this.tagConsumer.nextMessage();

		assertThat(message1.getAppearance(), is(RF_TAG_APPEARED));
		assertThat(message1.getRfTag(), sameInstance(tag));

		this.tagConsumer.noMoreMessages();
	}

	@After
	public void stop() {
		this.collectorConsumer.handle().unsubscribe();
	}

}
