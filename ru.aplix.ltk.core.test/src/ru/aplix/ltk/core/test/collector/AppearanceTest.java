package ru.aplix.ltk.core.test.collector;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;

import org.junit.Before;
import org.junit.Test;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.reader.RfTag;
import ru.aplix.ltk.core.test.reader.TestDataMessage;
import ru.aplix.ltk.core.test.reader.TestRfReaderDriver;
import ru.aplix.ltk.core.test.reader.TestTag;


public class AppearanceTest {

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
	public void tagAppeared() {

		final RfTag tag = new TestTag();
		final TestDataMessage data = new TestDataMessage(
				currentTimeMillis(),
				tag,
				0,
				false);

		this.driver.sendData(data);

		final RfTagAppearanceMessage message = this.tagConsumer.nextMessage();

		assertThat(message.getAppearance(), is(RF_TAG_APPEARED));
		assertThat(message.getRfTag(), sameInstance(tag));
		this.tagConsumer.noMoreMessages();
	}

	@Test
	public void secondTagAppeared() {

		final RfTag tag1 = new TestTag(1);
		final RfTag tag2 = new TestTag(2);
		final TestDataMessage data1 = new TestDataMessage(
				currentTimeMillis(),
				tag1,
				0,
				false);
		final TestDataMessage data2 = new TestDataMessage(
				data1.getTimestamp(),
				tag2,
				0,
				false);

		this.driver.sendData(data1);
		this.driver.sendData(data2);

		final RfTagAppearanceMessage message1 = this.tagConsumer.nextMessage();

		assertThat(message1.getAppearance(), is(RF_TAG_APPEARED));
		assertThat(message1.getRfTag(), sameInstance(tag1));

		final RfTagAppearanceMessage message2 = this.tagConsumer.nextMessage();

		assertThat(message2.getAppearance(), is(RF_TAG_APPEARED));
		assertThat(message2.getRfTag(), sameInstance(tag2));

		this.tagConsumer.noMoreMessages();
	}

	@Test
	public void tagReadSecondTime() {

		final RfTag tag = new TestTag();
		final TestDataMessage data = new TestDataMessage(
				currentTimeMillis(),
				tag,
				0,
				false);

		this.driver.sendData(data);
		this.driver.sendData(data);

		final RfTagAppearanceMessage message = this.tagConsumer.nextMessage();

		assertThat(message.getAppearance(), is(RF_TAG_APPEARED));
		assertThat(message.getRfTag(), sameInstance(tag));
		this.tagConsumer.noMoreMessages();
	}

}
