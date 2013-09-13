package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfStoreEditor;


final class RfStoreEditorImpl<S extends RfSettings>
		implements RfStoreEditor<S> {

	private final RfStoreServiceImpl service;
	private final RfProvider<S> provider;
	private final RfStoreImpl<S> store;
	private S settings;
	private boolean active;

	RfStoreEditorImpl(RfStoreServiceImpl service, RfProvider<S> provider) {
		this.service = service;
		this.provider = provider;
		this.store = null;
		this.settings = provider.newSettings();
		this.active = false;
	}

	RfStoreEditorImpl(RfStoreImpl<S> store) {
		this.service = store.getService();
		this.provider = store.getRfProvider();
		this.store = store;
		this.settings = getRfProvider().copySettings(store.getRfSettings());
		this.active = store.isActive();
	}

	public final RfStoreServiceImpl getService() {
		return this.service;
	}

	@Override
	public final RfProvider<S> getRfProvider() {
		return this.provider;
	}

	public final RfStoreImpl<S> getStore() {
		return this.store;
	}

	@Override
	public S getRfSettings() {
		return this.settings;
	}

	@Override
	public void setRfSettings(S settings) {
		this.settings =
				settings != null ? settings : getRfProvider().newSettings();
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public RfStore<S> save() {
		return getService().saveStore(this);
	}

}
