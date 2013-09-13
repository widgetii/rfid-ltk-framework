package ru.aplix.ltk.store.web.collector;

import java.net.MalformedURLException;
import java.net.URL;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfStoreEditor;


public class RfStoreBean {

	private int id;
	private String remoteURL;
	private boolean active;

	public RfStoreBean() {
	}

	public RfStoreBean(RfStore<HttpRfSettings> store) {

		final RfStoreEditor<HttpRfSettings> editor = store.modify();

		setId(store.getId());
		setActive(store.isActive());
		setRemoteURL(editor.getRfSettings().getCollectorURL().toString());
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRemoteURL() {
		return this.remoteURL;
	}

	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void edit(
			RfStoreEditor<HttpRfSettings> editor)
	throws MalformedURLException {

		final HttpRfSettings settings = editor.getRfSettings();

		settings.setCollectorURL(new URL(getRemoteURL()));
		editor.setActive(isActive());
	}

	public RfStoreBean update(RfStore<HttpRfSettings> store) {
		setActive(store.isActive());
		return this;
	}

}
