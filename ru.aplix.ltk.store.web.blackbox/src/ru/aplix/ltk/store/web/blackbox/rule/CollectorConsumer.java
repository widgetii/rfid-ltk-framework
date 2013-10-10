package ru.aplix.ltk.store.web.blackbox.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.source.RfStatusMessage;


public class CollectorConsumer
		extends TestConsumer<RfCollectorHandle, RfStatusMessage> {

	public TagConsumer requestTagAppearance() {

		final TagConsumer consumer = new TagConsumer();

		handle().requestTagAppearance(consumer);
		// Ensure client is connected.
		nextMessage();

		return consumer;
	}

	public RfStatusMessage waitFor(String readerId) {

		RfStatusMessage status = null;

		for (int i = 0; i < 3; ++i) {
			status = nextMessage();
			if (status.getRfReaderId().equals(readerId)) {
				break;
			}
		}

		assertThat("No status update received", status, notNullValue());

		if (status != null) {
			assertThat(status.getRfReaderId(), is(readerId));
		}

		return status;
	}

}
