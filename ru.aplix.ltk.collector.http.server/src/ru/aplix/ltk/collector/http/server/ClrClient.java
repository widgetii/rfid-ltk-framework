package ru.aplix.ltk.collector.http.server;

import ru.aplix.ltk.collector.http.ClrClientId;
import ru.aplix.ltk.collector.http.ClrClientRequest;


public class ClrClient {

	private final ClrProfile profile;
	private final ClrClientId clientId;

	public ClrClient(ClrProfile profile, ClrClientId id) {
		this.profile = profile;
		this.clientId = id;
	}

	public final ClrProfile getProfile() {
		return this.profile;
	}

	public final ClrClientId getId() {
		return this.clientId;
	}

	public void start(ClrClientRequest request) {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

}
