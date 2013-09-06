package ru.aplix.ltk.collector.http;

import java.util.UUID;

import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class ClrClientResponse implements Parameterized {

	private UUID clientId;

	public ClrClientResponse() {
	}

	public ClrClientResponse(Parameters parameters) {
		read(parameters);
	}

	public final UUID getClientId() {
		return this.clientId;
	}

	public final void setClientId(UUID clientId) {
		this.clientId = clientId;
	}

	@Override
	public void read(Parameters params) {
		setClientId(UUID.fromString(params.valueOf("clientId")));
	}

	@Override
	public void write(Parameters params) {
		params.set("clientId", getClientId());
	}

}
