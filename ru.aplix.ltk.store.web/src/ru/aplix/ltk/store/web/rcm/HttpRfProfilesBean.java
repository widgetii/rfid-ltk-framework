package ru.aplix.ltk.store.web.rcm;

import java.util.*;

import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;


public class HttpRfProfilesBean {

	private String error;
	private String selectedProfileId;
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

	public String getSelectedProfileId() {
		return this.selectedProfileId;
	}

	public void setSelectedProfileId(String selectedProfileId) {
		this.selectedProfileId = selectedProfileId;
	}

	public void addProfiles(
			Map<ClrProfileId, HttpRfProfile<?>> profiles) {

		final TreeMap<ClrProfileId, HttpRfProfileBean> beans = new TreeMap<>();

		for (HttpRfProfile<?> profile : profiles.values()) {
			beans.put(profile.getProfileId(), new HttpRfProfileBean(profile));
		}

		this.profiles.ensureCapacity(this.profiles.size() + beans.size());
		this.profiles.addAll(beans.values());
	}

}
