package ru.aplix.ltk.collector.http.server.sender;

import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.server.ClrClient;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;


public class TagAppearanceSender extends NoResponseMessageSender {

	private final RfTagAppearanceMessage message;

	public TagAppearanceSender(
			ClrClient<?> client,
			RfTagAppearanceMessage message) {
		super(client);
		this.message = message;
	}

	@Override
	public Boolean call() throws Exception {
		return post("tag", new RfTagAppearanceRequest(this.message));
	}

}
