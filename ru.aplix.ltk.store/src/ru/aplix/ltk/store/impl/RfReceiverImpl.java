package ru.aplix.ltk.store.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfTagQuery;
import ru.aplix.ltk.store.impl.monitor.RfReceiverMtr;
import ru.aplix.ltk.store.impl.monitor.RfReceiverMtrTarget;
import ru.aplix.ltk.store.impl.persist.RfReceiverData;
import ru.aplix.ltk.store.impl.persist.RfTagEventData;


final class RfReceiverImpl<S extends RfSettings> implements RfReceiver<S> {

	static <S extends RfSettings> RfReceiverImpl<S> rfReceiver(
			RfStoreImpl store,
			RfReceiverData data) {

		final RfProvider<S> provider = store.providerById(data.getProvider());

		if (provider == null) {
			return null;
		}

		return new RfReceiverImpl<>(store, provider, data);
	}

	private final RfStoreImpl store;
	private int id;
	private final RfProvider<S> provider;
	private volatile RfReceiverMtr monitoring;
	private volatile RfReceiverState<S> state;

	RfReceiverImpl(RfStoreImpl store, RfProvider<S> provider) {
		this.store = store;
		this.provider = provider;
		this.state = new RfReceiverState<>(this);
		this.monitoring = null;
		this.monitoring =
				store.getMonitor().monitoringOf(new RfReceiverMtrTarget(this));
	}

	private RfReceiverImpl(
			RfStoreImpl store,
			RfProvider<S> provider,
			RfReceiverData data) {
		this.store = store;
		this.provider = provider;
		this.id = data.getId();
		this.state = new RfReceiverState<>(this);
		this.monitoring =
				store.getMonitor().monitoringOf(new RfReceiverMtrTarget(this));
		this.state.load(data);
	}

	public final RfStoreImpl getRfStore() {
		return this.store;
	}

	public final RfReceiverMtr getMonitoring() {
		return this.monitoring;
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
	public List<RfTagEventData> loadEvents(long fromEventId, int limit) {
		try {
			return getRfStore().loadEvents(this, fromEventId, limit);
		} catch (RuntimeException | Error e) {
			getMonitoring().unexpectedError("Error while loading events", e);
			throw e;
		}
	}

	@Override
	public RfTagQuery tagQuery() {
		return new RfTagQueryImpl(getRfStore()).setReceiverId(getId());
	}

	@Override
	public RfReceiverEditorImpl<S> modify() {
		return new RfReceiverEditorImpl<>(this);
	}

	public final S getRfSettings() {
		return this.state.getRfSettings();
	}

	@Override
	public void delete(boolean deleteTags) {
		getMonitoring().delete(deleteTags);
		try {
			shutdown();
			getRfStore().deleteReceiver(this, deleteTags);
		} catch (Throwable e) {
			getMonitoring().unexpectedError("Failed to delete", e);
			throw e;
		}
	}

	@Override
	public String toString() {
		if (this.id == 0) {
			return "RFID Receiver";
		}
		if (this.state == null) {
			return "RFID Receiver (#" + this.id + ')';
		}
		return "RFID Receiver ("
				+ this.state.getRfSettings().getTargetId()
				+ " #" + this.id + ')';
	}

	synchronized void shutdown() {
		this.state.shutdown();
	}

	synchronized RfReceiverState<S> update(RfReceiverEditorImpl<S> editor) {

		final RfReceiverState<S> prevState = this.state;

		this.state = this.state.update(editor);
		if (editor.getRfReceiver() == null) {
			create(editor);
		} else {
			merge(editor);
		}

		return prevState;
	}

	synchronized void update(RfReceiverState<S> state) {
		this.state = state;
	}

	private void create(RfReceiverEditorImpl<S> editor) {

		final RfReceiverData data = new RfReceiverData();

		editor.save(data);

		final EntityManager em = getRfStore().getEntityManager();

		em.persist(data);
		em.refresh(data);

		this.id = data.getId();
		this.monitoring =
				getRfStore()
				.getMonitor()
				.monitoringOf(new RfReceiverMtrTarget(this));
	}

	private void merge(RfReceiverEditorImpl<S> editor) {

		final RfReceiverData data = getRfStore().getEntityManager().find(
				RfReceiverData.class,
				getId(),
				LockModeType.PESSIMISTIC_WRITE);

		editor.save(data);
	}

}
