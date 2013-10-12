package ru.aplix.ltk.collector.http.server.sender;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.server.ClrClient;
import ru.aplix.ltk.core.source.RfStatusMessage;


public class StatusSender extends NoResponseMessageSender {

	private final RfStatusMessage status;

	public StatusSender(ClrClient<?> client, RfStatusMessage status) {
		super(client);
		this.status = status;
	}

	@Override
	public void run() {
		post(new RfStatusRequest(getClient().getId(), this.status));
	}

}
