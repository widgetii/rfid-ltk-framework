package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfReceiverEditor;


final class RfReceiverImpl<S extends RfSettings> implements RfReceiver<S> {

	private final RfStoreImpl store;
	private final int id;
	private final RfProvider<S> provider;
	private volatile RfReceiverState<S> state;

	RfReceiverImpl(RfStoreImpl store, int id, RfProvider<S> provider) {
		this.store = store;
		this.id = id;
		this.provider = provider;
		this.state = new RfReceiverState<>(this);
	}

	public final RfStoreImpl getRfStore() {
		return this.store;
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
	public RfReceiverEditorImpl<S> modify() {
		return new RfReceiverEditorImpl<>(this);
	}

	public final S getRfSettings() {
		return this.state.getRfSettings();
	}

	@Override
	public void delete() {
		getRfStore().deleteReceiver(this);
		synchronized (this) {
			this.state.shutdown();
		}
	}

	synchronized void update(RfReceiverEditor<S> editor) {
		this.state = this.state.update(editor);
	}

}
