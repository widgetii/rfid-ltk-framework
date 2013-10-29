package ru.aplix.ltk.collector.http.server;

import static java.util.Collections.synchronizedMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.aplix.ltk.collector.http.ClrClientId;
import ru.aplix.ltk.collector.http.ClrClientRequest;
import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameters;
import ru.aplix.ltk.osgi.Logger;


public class ClrProfile<S extends RfSettings> {

	private final AllClrProfiles allProfiles;
	private final ClrProfileConfig<S> config;
	private final Map<UUID, ClrClient<S>> clients =
			synchronizedMap(new HashMap<UUID, ClrClient<S>>(1));
	private ClrAutostart autostart;

	public ClrProfile(AllClrProfiles allProfiles, ClrProfileConfig<S> config) {
		this.allProfiles = allProfiles;
		this.config = config;
	}

	public final AllClrProfiles allProfiles() {
		return this.allProfiles;
	}

	public final ClrProfileConfig<S> getConfig() {
		return this.config;
	}

	public final RfProvider<S> getProvider() {
		return getConfig().getProvider();
	}

	public final ClrProfileId getProfileId() {
		return getConfig().getProfileId();
	}

	public final Parameters getParameters() {
		return getConfig().getParameters();
	}

	public final Logger log() {
		return allProfiles().log();
	}

	public void autostart() {
		this.autostart = new ClrAutostart(this);
		this.autostart.start();
	}

	public final ClrClient<S> connectClient(
			UUID clientUUID,
			ClrClientRequest request) {
		return addClient(
				new ClrClientId(getProfileId(), clientUUID),
				request);
	}

	public final ClrClient<S> reconnectClient(
			UUID id,
			ClrClientRequest request) {

		final ClrClient<S> removed = removeClient(id);

		if (removed == null) {
			return null;
		}

		return addClient(removed.getId(), request);
	}

	public final boolean disconnectClient(UUID id) {
		return removeClient(id) != null;
	}

	public void dispose() {
		if (this.autostart != null) {
			this.autostart.stop();
		}
		synchronized (this.clients) {
			for (ClrClient<S> client : this.clients.values()) {
				client.stop();
			}
			this.clients.clear();
		}
	}

	public RfConnection connect() {

		final RfProvider<S> provider = getProvider();
		final S settings = provider.newSettings();

		settings.read(getParameters());

		return provider.connect(settings);
	}

	@Override
	public String toString() {
		if (this.config == null) {
			return super.toString();
		}
		return this.config.toString();
	}

	private ClrClient<S> addClient(
			ClrClientId clientId,
			ClrClientRequest request) {

		final ClrClient<S> client = new ClrClient<>(this, clientId);

		synchronized (this.clients) {

			final ClrClient<S> existing =
					this.clients.put(clientId.getUUID(), client);

			if (existing != null) {
				this.clients.put(clientId.getUUID(), existing);
				throw new IllegalArgumentException(
						"Client " + clientId + " already connected");
			}
		}

		client.start(request);

		return client;
	}

	private ClrClient<S> removeClient(UUID id) {

		final ClrClient<S> oldClient = this.clients.remove(id);

		if (oldClient == null) {
			return null;
		}

		oldClient.stop();

		return oldClient;
	}

}
