package ru.aplix.ltk.store.web.rcm;

import java.net.URL;
import java.util.*;

import ru.aplix.ltk.collector.http.ClrAddress;
import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.store.RfReceiver;


public class HttpRfServersBean {

	private final ArrayList<String> servers = new ArrayList<>();

	public List<String> getServers() {
		return this.servers;
	}

	public void addReceivers(Collection<? extends RfReceiver<?>> receivers) {

		final TreeSet<String> servers = new TreeSet<>();

		for (RfReceiver<?> receiver : receivers) {
			addReceiver(servers, receiver);
		}

		this.servers.addAll(servers);
	}

	private void addReceiver(TreeSet<String> servers, RfReceiver<?> receiver) {

		final String serverURL = serverURL(receiver);

		if (serverURL == null) {
			return;
		}

		servers.add(serverURL);
	}

	private String serverURL(RfReceiver<?> receiver) {
		if (receiver.getRfProvider().getSettingsType()
				!= HttpRfSettings.class) {
			return null;
		}

		@SuppressWarnings("unchecked")
		final RfReceiver<HttpRfSettings> httpReceiver =
				(RfReceiver<HttpRfSettings>) receiver;
		final HttpRfSettings settings = httpReceiver.modify().getRfSettings();
		final URL collectorURL = settings.getCollectorURL();

		if (collectorURL == null) {
			return null;
		}

		return new ClrAddress(collectorURL).getServerURL().toExternalForm();
	}

}
