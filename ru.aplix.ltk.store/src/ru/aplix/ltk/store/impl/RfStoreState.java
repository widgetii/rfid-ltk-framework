package ru.aplix.ltk.store.impl;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.store.RfStoreEditor;


final class RfStoreState<S extends RfSettings>
		implements MsgConsumer<RfCollectorHandle, RfStatusMessage>, Cloneable {

	private final RfStoreImpl<S> store;
	private S settings;
	private volatile RfCollector collector;
	private RfCollectorHandle handle;

	RfStoreState(RfStoreImpl<S> store) {
		this.store = store;
		this.settings = store.getRfProvider().newSettings();
	}

	public final RfProvider<S> getRfProvider() {
		return getRfStore().getRfProvider();
	}

	public final RfStoreImpl<S> getRfStore() {
		return this.store;
	}

	public final S getRfSettings() {
		return this.settings;
	}

	public final boolean isActive() {
		return this.collector != null;
	}

	public final RfCollector getRfCollector() {
		return this.collector;
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

	@SuppressWarnings("unchecked")
	@Override
	protected RfStoreState<S> clone() {
		try {
			return (RfStoreState<S>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	RfStoreState<S> update(RfStoreEditor<S> editor) {

		boolean stop = false;
		final S newSettings =
				getRfProvider().copySettings(editor.getRfSettings());
		final boolean wasActive = isActive();
		final boolean settingsChanged =
				!newSettings.equals(getRfSettings());

		if (settingsChanged) {
			stop = wasActive;
		}
		if (stop) {
			stop();
		}

		final RfStoreState<S> newState = clone();

		if (settingsChanged) {
			newState.settings = newSettings;
		}
		if (editor.isActive() && (stop || !wasActive)) {
			newState.start();
		}

		return newState;
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
