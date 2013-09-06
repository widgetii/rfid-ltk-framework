package ru.aplix.ltk.collector.http.server;

import java.util.UUID;

import ru.aplix.ltk.collector.http.ClrClientRequest;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.util.Parameters;


public class ClrProfile {

	private final RfProvider<?> provider;
	private final Parameters parameters;

	public ClrProfile(RfProvider<?> provider, Parameters parameters) {
		this.provider = provider;
		this.parameters = parameters;
	}

	public final RfProvider<?> getProvider() {
		return this.provider;
	}

	public final Parameters getParameters() {
		return this.parameters;
	}

	public ClrClient createClient(ClrClientRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClrClient updateClient(UUID id, ClrClientRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteClient(UUID id) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}
