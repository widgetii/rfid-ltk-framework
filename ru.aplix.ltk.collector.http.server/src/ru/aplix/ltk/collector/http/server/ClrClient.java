package ru.aplix.ltk.collector.http.server;

import java.util.UUID;


public class ClrClient {

	private final UUID id;

	public ClrClient(UUID id) {
		this.id = id;
	}

	public final UUID getId() {
		return this.id;
	}

}
