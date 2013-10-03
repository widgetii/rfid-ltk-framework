package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameters;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfReceiverEditor;
import ru.aplix.ltk.store.impl.persist.RfReceiverData;


final class RfReceiverEditorImpl<S extends RfSettings>
		implements RfReceiverEditor<S> {

	private final RfStoreImpl store;
	private final RfProvider<S> provider;
	private final RfReceiverImpl<S> receiver;
	private S settings;
	private boolean active;

	RfReceiverEditorImpl(RfStoreImpl store, RfProvider<S> provider) {
		this.store = store;
		this.provider = provider;
		this.receiver = null;
		this.settings = provider.newSettings();
		this.active = false;
	}

	RfReceiverEditorImpl(RfReceiverImpl<S> reseiver) {
		this.store = reseiver.getRfStore();
		this.provider = reseiver.getRfProvider();
		this.receiver = reseiver;
		this.settings = getRfProvider().copySettings(reseiver.getRfSettings());
		this.active = reseiver.isActive();
	}

	public final RfStoreImpl getRfStore() {
		return this.store;
	}

	@Override
	public final RfProvider<S> getRfProvider() {
		return this.provider;
	}

	public final RfReceiverImpl<S> getRfReceiver() {
		return this.receiver;
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
	public RfReceiver<S> save() {
		return getRfStore().saveReceiver(this);
	}

	void save(RfReceiverData data) {
		data.setActive(isActive());
		data.setProvider(getRfProvider().getId());
		data.setSettings(new Parameters().setBy(getRfSettings()).urlEncode());
	}

}
