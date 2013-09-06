package ru.aplix.ltk.collector.http;

import java.util.UUID;

import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class ClrClientResponse implements Parameterized {

	private UUID clientUUID;

	public ClrClientResponse() {
	}

	public ClrClientResponse(Parameters parameters) {
		read(parameters);
	}

	public final UUID getClientUUID() {
		return this.clientUUID;
	}

	public final void setClientUUID(UUID uuid) {
		this.clientUUID = uuid;
	}

	@Override
	public void read(Parameters params) {
		setClientUUID(UUID.fromString(params.valueOf("clientUUID")));
	}

	@Override
	public void write(Parameters params) {
		params.set("clientUUID", getClientUUID());
	}

}
