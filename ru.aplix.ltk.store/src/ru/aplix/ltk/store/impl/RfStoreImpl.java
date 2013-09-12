package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfStore;


final class RfStoreImpl<S extends RfSettings> implements RfStore<S> {

	private final RfStoreServiceImpl service;
	private final int id;
	private final RfProvider<S> provider;
	private S settings;

	RfStoreImpl(
			RfStoreServiceImpl service,
			int id,
			RfProvider<S> provider) {
		this.service = service;
		this.id = id;
		this.provider = provider;
		this.settings = provider.newSettings();
	}

	public final RfStoreServiceImpl getService() {
		return this.service;
	}

	@Override
	public final int getId() {
		return this.id;
	}

	@Override
	public final RfProvider<S> getRfProvider() {
		return this.provider;
	}

	@Override
	public final S getRfSettings() {
		return this.settings;
	}

	@Override
	public final void setRfSettings(S settings) {
		this.settings =
				settings != null ? settings : getRfProvider().newSettings();
	}

	@Override
	public void save() {
		getService().saveStore(this);
	}

	@Override
	public void delete() {
		getService().deleteStore(this);
	}

}
