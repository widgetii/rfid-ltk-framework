package ru.aplix.ltk.store.web.rcm;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.collector.http.client.HttpRfServer;
import ru.aplix.ltk.store.web.ErrorBean;
import ru.aplix.ltk.store.web.receiver.RfReceiverDesc;


public class HttpRfProfilesBean implements ErrorBean {

	private String error;
	private String selected;
	private String collectorURL;
	private final ArrayList<HttpRfProfileBean> profiles = new ArrayList<>();
	private boolean invalidServer;

	public String getCollectorURL() {
		return this.collectorURL;
	}

	@Override
	public String getError() {
		return this.error;
	}

	@Override
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
		// Use original URL in case of request failure.
		this.collectorURL =
				server.getAddress().getURL().toExternalForm();

		final Map<ClrProfileId, HttpRfProfile<?>> profiles =
				server.loadProfiles();
		final Map<String, RfReceiverDesc> receivers =
				controller.receiversByURL();
		final TreeMap<ClrProfileId, HttpRfProfileBean> beans = new TreeMap<>();

		for (HttpRfProfile<?> profile : profiles.values()) {

			final HttpRfProfileBean bean = new HttpRfProfileBean(profile);
			final ClrProfileId profileId = profile.getProfileId();

			if (!profileId.isDefault()) {

				final URL url = server.getAddress().clientURL(profileId);

				bean.setReceiver(receivers.get(url.toExternalForm()));
			}

			bean.setSettings(controller.uiSettings(profile));

			beans.put(profileId, bean);
		}

		this.profiles.addAll(beans.values());

		// Use proper collector URL only if request succeed.
		this.collectorURL =
				server.getAddress().getCollectorURL().toExternalForm();
	}

}
