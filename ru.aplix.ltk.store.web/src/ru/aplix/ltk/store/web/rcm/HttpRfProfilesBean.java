package ru.aplix.ltk.store.web.rcm;

import static ru.aplix.ltk.store.web.receiver.RfReceiverDesc.rfReceiverDesc;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.collector.http.client.HttpRfServer;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.web.rcm.ui.RcmUIController;
import ru.aplix.ltk.store.web.rcm.ui.RcmUISettings;
import ru.aplix.ltk.store.web.receiver.RfReceiverDesc;


public class HttpRfProfilesBean {

	private String error;
	private String selected;
	private String collectorURL;
	private final ArrayList<HttpRfProfileBean> profiles = new ArrayList<>();
	private boolean invalidServer;

	public String getCollectorURL() {
		return this.collectorURL;
	}

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

	void loadProfiles(
			RcmController controller,
			HttpRfServer server)
	throws IOException {

		final RfStore rfStore = controller.rfStore();

		// Use original URL in case of request failure.
		this.collectorURL =
				server.getAddress().getURL().toExternalForm();

		final Map<ClrProfileId, HttpRfProfile<?>> profiles =
				server.loadProfiles();
		final Map<String, RfReceiverDesc> receivers = receivers(rfStore);
		final TreeMap<ClrProfileId, HttpRfProfileBean> beans = new TreeMap<>();

		for (HttpRfProfile<?> profile : profiles.values()) {

			final HttpRfProfileBean bean = new HttpRfProfileBean(profile);
			final ClrProfileId profileId = profile.getProfileId();

			if (!profileId.isDefault()) {

				final URL url = server.getAddress().clientURL(profileId);

				bean.setReceiver(receivers.get(url.toExternalForm()));
			}

			bean.setSettings(uiSettings(controller, profile));

			beans.put(profileId, bean);
		}

		this.profiles.addAll(beans.values());

		// Use proper collector URL only if request succeed.
		this.collectorURL =
				server.getAddress().getCollectorURL().toExternalForm();
	}

	private static <S extends RfSettings, R extends RcmUISettings> R uiSettings(
			RcmController controller,
			HttpRfProfile<S> profile) {

		@SuppressWarnings("unchecked")
		final RcmUIController<S, R> uiController =
				(RcmUIController<S, R>) controller.uiController(
						profile.getProfileId().getProviderId());

		return uiController.uiSettings(profile);
	}

	private static Map<String, RfReceiverDesc> receivers(RfStore rfStore) {

		final Collection<? extends RfReceiver<?>> list =
				rfStore.allRfReceivers();
		final HashMap<String, RfReceiverDesc> map =
				new HashMap<>(list.size());

		for (RfReceiver<?> receiver : list) {

			final RfReceiverDesc desc = rfReceiverDesc(receiver);
			final String url = desc.getRemoteURL();

			if (url != null) {

				final RfReceiverDesc old = map.put(url, desc);

				if (old != null && old.getId() < desc.getId()) {
					map.put(url, old);
				}
			}
		}

		return map;
	}

}
