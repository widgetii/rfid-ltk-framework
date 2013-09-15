package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfStoreEditor;


final class RfStoreImpl<S extends RfSettings> implements RfStore<S> {

	private final RfStoreServiceImpl service;
	private final int id;
	private final RfProvider<S> provider;
	private volatile RfStoreState<S> state;

	RfStoreImpl(
			RfStoreServiceImpl service,
			int id,
			RfProvider<S> provider) {
		this.service = service;
		this.id = id;
		this.provider = provider;
		this.state = new RfStoreState<>(this);
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
	public boolean isActive() {
		return this.state.isActive();
	}

	@Override
	public RfCollector getRfCollector() {
		return this.state.getRfCollector();
	}

	@Override
	public RfStatusMessage getLastStatus() {
		return this.state.getLastStatus();
	}

	@Override
	public RfStoreEditorImpl<S> modify() {
		return new RfStoreEditorImpl<>(this);
	}

	public final S getRfSettings() {
		return this.state.getRfSettings();
	}

	@Override
	public void delete() {
		getService().deleteStore(this);
		synchronized (this) {
			this.state.shutdown();
		}
	}

	synchronized void update(RfStoreEditor<S> editor) {
		this.state = this.state.update(editor);
	}

}
