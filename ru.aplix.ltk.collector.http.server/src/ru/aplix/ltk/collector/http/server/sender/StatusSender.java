package ru.aplix.ltk.collector.http.server.sender;

import static ru.aplix.ltk.collector.http.CollectorHttpConstants.CLR_HTTP_CLIENT_STATUS_PATH;
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
	public Boolean call() throws Exception {
		return post(
				CLR_HTTP_CLIENT_STATUS_PATH,
				new RfStatusRequest(this.status));
	}

}
