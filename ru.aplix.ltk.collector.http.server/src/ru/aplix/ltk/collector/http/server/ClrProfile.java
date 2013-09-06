package ru.aplix.ltk.collector.http.server;

import static java.util.Collections.synchronizedMap;
import static java.util.UUID.randomUUID;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.aplix.ltk.collector.http.ClrClientId;
import ru.aplix.ltk.collector.http.ClrClientRequest;
import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.util.Parameters;


public class ClrProfile {

	private final RfProvider<?> provider;
	private final ClrProfileId profileId;
	private final Parameters parameters;
	private final Map<UUID, ClrClient> clients =
			synchronizedMap(new HashMap<UUID, ClrClient>(1));

	public ClrProfile(
			RfProvider<?> provider,
			ClrProfileId profileId,
			Parameters parameters) {
		this.provider = provider;
		this.profileId = profileId;
		this.parameters = parameters;
	}

	public final RfProvider<?> getProvider() {
		return this.provider;
	}

	public final ClrProfileId getProfileId() {
		return this.profileId;
	}

	public final Parameters getParameters() {
		return this.parameters;
	}

	public ClrClient connectClient(ClrClientRequest request) {
		return addClient(
				new ClrClientId(getProfileId(), randomUUID()),
				request);
	}

	public ClrClient reconnectClient(UUID id, ClrClientRequest request) {

		final ClrClient removed = removeClient(id);

		if (removed == null) {
			return null;
		}

		return addClient(removed.getId(), request);
	}

	public boolean disconnectClient(UUID id) {
		return removeClient(id) != null;
	}

	public void dispose() {
		synchronized (this.clients) {
			for (ClrClient client : this.clients.values()) {
				client.stop();
			}
			this.clients.clear();
		}
	}

	private ClrClient addClient(
			ClrClientId clientId,
			ClrClientRequest request) {

		final ClrClient client = new ClrClient(this, clientId);

		this.clients.put(clientId.getUUID(), client);
		client.start(request);

		return client;
	}

	private ClrClient removeClient(UUID id) {

		final ClrClient oldClient = this.clients.remove(id);

		if (oldClient == null) {
			return null;
		}

		oldClient.stop();

		return oldClient;
	}

}
