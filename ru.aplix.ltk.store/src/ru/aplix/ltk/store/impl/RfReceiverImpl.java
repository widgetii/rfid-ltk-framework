package ru.aplix.ltk.store.impl;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.impl.persist.RfReceiverData;


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
	private volatile RfReceiverState<S> state;

	RfReceiverImpl(RfStoreImpl store, RfProvider<S> provider) {
		this.store = store;
		this.provider = provider;
		this.state = new RfReceiverState<>(this);
	}

	private RfReceiverImpl(
			RfStoreImpl store,
			RfProvider<S> provider,
			RfReceiverData data) {
		this.store = store;
		this.provider = provider;
		this.id = data.getId();
		this.state = new RfReceiverState<>(this);
		this.state.load(data);
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
		shutdown();
		getRfStore().deleteReceiver(this);
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
	}

	private void merge(RfReceiverEditorImpl<S> editor) {

		final RfReceiverData data = getRfStore().getEntityManager().find(
				RfReceiverData.class,
				getId(),
				LockModeType.PESSIMISTIC_WRITE);

		editor.save(data);
	}

}
