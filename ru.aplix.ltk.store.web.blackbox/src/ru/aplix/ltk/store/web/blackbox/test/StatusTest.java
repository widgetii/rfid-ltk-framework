package ru.aplix.ltk.store.web.blackbox.test;

import static java.lang.System.nanoTime;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ru.aplix.ltk.core.source.RfStatus.RF_ERROR;
import static ru.aplix.ltk.core.source.RfStatus.RF_READY;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Rule;
import org.junit.Test;

import ru.aplix.ltk.collector.http.ParametersEntity;
import ru.aplix.ltk.collector.http.RemoteClrException;
import ru.aplix.ltk.collector.http.RfStatusRequest;
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

		request.setRfReaderId("blackbox-ready-" + nanoTime());
		request.setRfStatus(RF_READY);

		post.setEntity(new ParametersEntity(request));

		final HttpResponse response =
				this.connection.getHttpClient().execute(post);

		assertThat(
				response.getStatusLine().getReasonPhrase(),
				response.getStatusLine().getStatusCode(),
				is(200));

		waitForStatus(consumer, request);
	}

	@Test
	public void error() throws Exception {

		final CollectorConsumer consumer = this.receiver.subscribe();
		final HttpPost post = this.connection.post("status");
		final RfStatusRequest request = new RfStatusRequest();

		request.setRfReaderId("blackbox-error-" + nanoTime());
		request.setRfStatus(RF_ERROR);
		request.setErrorMessage("Blackbox error");

		final Exception exception = new Exception("Blackbox exception");

		request.setCause(exception);

		post.setEntity(new ParametersEntity(request));

		final HttpResponse response =
				this.connection.getHttpClient().execute(post);

		assertThat(
				response.getStatusLine().getReasonPhrase(),
				response.getStatusLine().getStatusCode(),
				is(200));

		final RfStatusMessage status = waitForStatus(consumer, request);

		assertThat(status.getErrorMessage(), is(request.getErrorMessage()));

		final RemoteClrException remote =
				(RemoteClrException) status.getCause();

		assertThat(remote.getMessage(), is(exception.getMessage()));
		assertThat(
				remote.getRemoteExceptionClassName(),
				is(exception.getClass().getName()));
	}

	private RfStatusMessage waitForStatus(
			final CollectorConsumer consumer,
			final RfStatusRequest request) {

		final RfStatusMessage status =
				consumer.waitFor(request.getRfReaderId());

		assertThat(status.getRfStatus(), is(request.getRfStatus()));

		return status;
	}

}
