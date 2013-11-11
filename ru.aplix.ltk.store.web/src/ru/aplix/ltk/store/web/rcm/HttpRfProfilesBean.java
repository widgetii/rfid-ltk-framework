package ru.aplix.ltk.store.web.rcm;

import static ru.aplix.ltk.store.web.receiver.RfReceiverDesc.rfReceiverDesc;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.collector.http.client.HttpRfServer;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.web.receiver.RfReceiverDesc;


public class HttpRfProfilesBean {

	private String error;
	private String selected;
	private final ArrayList<HttpRfProfileBean> profiles = new ArrayList<>();
	private boolean invalidServer;

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isInvalidServer() {
		return this.invalidServer;
	}

	public void setInvalidServer(boolean invalidServer) {
		this.invalidServer = invalidServer;
	}

	public List<HttpRfProfileBean> getProfiles() {
		return this.profiles;
	}

	public String getSelected() {
		return this.selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public void loadProfiles(
			HttpRfServer server,
			RfStore store)
	throws IOException {

		final Map<ClrProfileId, HttpRfProfile<?>> profiles =
				server.loadProfiles();
		final Map<String, ? extends RfProvider<?>> providers =
				store.allRfProviders();
		final Map<String, RfReceiverDesc> receivers = receivers(store);
		final TreeMap<ClrProfileId, HttpRfProfileBean> beans = new TreeMap<>();

		for (HttpRfProfile<?> profile : profiles.values()) {

			final HttpRfProfileBean bean = new HttpRfProfileBean(profile);
			final RfProvider<?> provider =
					providers.get(profile.getProvider().getId());

			if (provider != null) {
				bean.setProvider(new RfProviderDesc(provider));
			}

			final ClrProfileId profileId = profile.getProfileId();

			if (!profileId.isDefault()) {

				final URL url = server.getAddress().clientURL(profileId);

				bean.setReceiver(receivers.get(url.toExternalForm()));
			}

			beans.put(profile.getProfileId(), bean);
		}

		this.profiles.ensureCapacity(this.profiles.size() + beans.size());
		this.profiles.addAll(beans.values());
	}

	private static Map<String, RfReceiverDesc> receivers(RfStore store) {

		final Collection<? extends RfReceiver<?>> list = store.allRfReceivers();
		final HashMap<String, RfReceiverDesc> map = new HashMap<>(list.size());

		for (RfReceiver<?> receiver : list) {

			final RfReceiverDesc desc = rfReceiverDesc(receiver);
			final String url = desc.getRemoteURL();

			if (url != null) {
				map.put(url, desc);
			}
		}

		return map;
	}

}
