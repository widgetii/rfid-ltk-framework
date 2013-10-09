package ru.aplix.ltk.store.web.blackbox.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.core.source.RfStatus;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.store.web.blackbox.rule.CollectorConsumer;
import ru.aplix.ltk.store.web.blackbox.rule.HttpConnection;
import ru.aplix.ltk.store.web.blackbox.rule.TestReceiver;


public class StatusTest {

	@Rule
	public TestReceiver receiver = new TestReceiver("blackbox", true);

	@Rule
	public HttpConnection connection = new HttpConnection();

	@Test
	public void ready() throws Exception {

		final CollectorConsumer consumer = this.receiver.subscribe();
		final HttpPost post = this.connection.post("status");
		final RfStatusRequest request = new RfStatusRequest();

		request.setRfReaderId("blackbox");
		request.setRfStatus(RfStatus.RF_READY);

		post.setEntity(new ParametersEntity(request));

		final HttpResponse response =
				this.connection.getHttpClient().execute(post);

		assertThat(
				response.getStatusLine().getReasonPhrase(),
				response.getStatusLine().getStatusCode(),
				is(200));


		RfStatusMessage status = null;

		for (int i = 0; i < 3; ++i) {
			status = consumer.nextMessage();
			if (status == null) {
				break;
			}
			if (status.getRfReaderId().equals(request.getRfReaderId())) {
				break;
			}
		}

		assertThat("No status update received", status, notNullValue());
		if (status == null) {
			return;
		}
		assertThat(status.getRfReaderId(), is(request.getRfReaderId()));
		assertThat(status.getRfStatus(), is(request.getRfStatus()));
	}

}
