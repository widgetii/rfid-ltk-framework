package ru.aplix.ltk.collector.http.server.sender;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import ru.aplix.ltk.collector.http.server.ClrClient;
import ru.aplix.ltk.collector.http.server.MessageSender;


public abstract class NoResponseMessageSender extends MessageSender<Boolean> {

	public NoResponseMessageSender(ClrClient<?> client) {
		super(client);
	}

	@Override
	protected Boolean handleEntity(
			HttpEntity entity)
	throws ClientProtocolException, IOException {
		EntityUtils.consume(entity);
		return Boolean.TRUE;
	}

}
