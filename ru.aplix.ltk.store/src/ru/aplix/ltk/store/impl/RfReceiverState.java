package ru.aplix.ltk.store.impl;

import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.util.Parameters;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.store.RfReceiverEditor;
import ru.aplix.ltk.store.impl.persist.RfReceiverData;


final class RfReceiverState<S extends RfSettings>
		implements MsgConsumer<RfCollectorHandle, RfStatusMessage>, Cloneable {

	private final RfReceiverImpl<S> receiver;
	private S settings;
	private volatile RfCollector collector;
	private RfCollectorHandle handle;
	private volatile RfStatusMessage lastStatus;

	RfReceiverState(RfReceiverImpl<S> receiver) {
		this.receiver = receiver;
		this.settings = receiver.getRfProvider().newSettings();
	}

	public final RfProvider<S> getRfProvider() {
		return getRfReceiver().getRfProvider();
	}

	public final RfReceiverImpl<S> getRfReceiver() {
		return this.receiver;
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

	public final RfStatusMessage getLastStatus() {
		return this.lastStatus;
	}

	@Override
	public void consumerSubscribed(RfCollectorHandle handle) {
		this.handle = handle;

		final RfReceiverTagListener tagListener =
				new RfReceiverTagListener(getRfReceiver());

		handle.requestTagAppearance(
				tagListener,
				tagListener.retrieveLastEventId());
	}

	@Override
	public void messageReceived(RfStatusMessage message) {
		this.lastStatus = message;
	}

	@Override
	public void consumerUnsubscribed(RfCollectorHandle handle) {
		stop();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RfReceiverState<S> clone() {
		try {
			return (RfReceiverState<S>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	void load(RfReceiverData data) {

		final Parameters params =
				new Parameters().urlDecode(data.getSettings());
		final S settings = getRfProvider().newSettings();

		settings.read(params);

		this.settings = settings;
		if (data.isActive()) {
			start();
		}
	}

	RfReceiverState<S> update(RfReceiverEditor<S> editor) {

		final S newSettings =
				getRfProvider().copySettings(editor.getRfSettings());
		final boolean wasActive = isActive();
		boolean stop = wasActive && !editor.isActive();
		final boolean settingsChanged =
				!newSettings.equals(getRfSettings());

		if (settingsChanged) {
			stop = wasActive;
		}
		if (stop) {
			stop();
		}

		final RfReceiverState<S> newState = clone();

		if (settingsChanged) {
			newState.settings = newSettings;
		}
		if (editor.isActive() && (stop || !wasActive)) {
			registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					newState.start();
				}
			});
		}

		return newState;
	}

	void shutdown() {
		if (isActive()) {
			stop();
		}
	}

	private void start() {
		this.lastStatus = null;
		this.collector =
				getRfProvider().connect(getRfSettings()).getCollector();
		this.collector.subscribe(this);
	}

	private void stop() {
		try {
			if (this.handle != null) {
				this.handle.unsubscribe();
			}
		} finally {
			this.lastStatus = null;
			this.collector = null;
		}
	}

}
