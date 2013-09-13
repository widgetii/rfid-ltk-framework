package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfStoreEditor;


final class RfStoreImpl<S extends RfSettings>
		implements RfStore<S>, MsgConsumer<RfCollectorHandle, RfStatusMessage> {

	private final RfStoreServiceImpl service;
	private final int id;
	private final RfProvider<S> provider;
	private S settings;
	private volatile RfCollector collector;
	private RfCollectorHandle handle;

	RfStoreImpl(
			RfStoreServiceImpl service,
			int id,
			RfProvider<S> provider) {
		this.service = service;
		this.id = id;
		this.provider = provider;
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
		return this.collector != null;
	}

	@Override
	public RfCollector getRfCollector() {
		return this.collector;
	}

	@Override
	public RfStoreEditorImpl<S> modify() {
		return new RfStoreEditorImpl<>(this);
	}

	public final S getRfSettings() {
		return this.settings;
	}

	@Override
	public void delete() {
		getService().deleteStore(this);
	}

	@Override
	public void consumerSubscribed(RfCollectorHandle handle) {
		this.handle = handle;
		handle.requestTagAppearance(new RfStoreTagListener());
	}

	@Override
	public void messageReceived(RfStatusMessage message) {
		// TODO handle messages
	}

	@Override
	public void consumerUnsubscribed(RfCollectorHandle handle) {
		this.handle = null;
	}

	void update(RfStoreEditor<S> editor) {

		boolean stop = false;
		final S newSettings =
				getRfProvider().copySettings(editor.getRfSettings());

		synchronized (this) {

			final boolean wasActive = isActive();
			final boolean settingsChanged =
					!newSettings.equals(this.settings);

			if (settingsChanged) {
				stop = wasActive;
				this.settings = newSettings;
			}
			if (stop) {
				stop();
			}
			if (editor.isActive() && (stop || !wasActive)) {
				start();
			}
		}
	}

	private void start() {
		this.collector =
				getRfProvider().connect(getRfSettings()).getCollector();
		this.collector.subscribe(this);
	}

	private void stop() {
		this.collector = null;
		if (this.handle != null) {
			this.handle.unsubscribe();
		}
	}

}
