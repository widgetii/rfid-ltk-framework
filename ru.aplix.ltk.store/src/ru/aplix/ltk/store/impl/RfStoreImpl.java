package ru.aplix.ltk.store.impl;

import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;
import static ru.aplix.ltk.store.impl.RfReceiverImpl.rfReceiver;

import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.impl.persist.RfReceiverData;


@Component("rfStore")
public class RfStoreImpl implements RfStore {

	private final ConcurrentHashMap<Integer, RfReceiverImpl<?>> receivers =
			new ConcurrentHashMap<>();

	@PersistenceUnit(unitName = "rfstore")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("rfProviders")
	private List<RfProvider<?>> providerList;

	private volatile boolean loaded;

	private volatile ConcurrentHashMap<String, RfProvider<?>> providers;

	@Override
	public Collection<? extends RfReceiverImpl<?>> allRfReceivers() {
		return this.receivers.values();
	}

	@Override
	public RfReceiver<?> rfReceiverById(int id) {
		return this.receivers.get(id);
	}

	@Override
	public <S extends RfSettings> RfReceiverEditorImpl<S> newRfReceiver(
			RfProvider<S> provider) {
		return new RfReceiverEditorImpl<>(this, provider);
	}

	public void providersUpdated(
			@SuppressWarnings("unused") RfProvider<?> target,
			@SuppressWarnings("unused") Dictionary<?, ?> properties) {
		this.providers = null;
		if (this.loaded) {
			loadReceivers();
		}
	}

	final EntityManager getEntityManager() {
		return this.entityManager;
	}

	@SuppressWarnings("unchecked")
	final <S extends RfSettings> RfProvider<S> providerById(
			String providerId) {
		return (RfProvider<S>) providersById().get(providerId);
	}

	@Transactional
	<S extends RfSettings> RfReceiverImpl<S> saveReceiver(
			RfReceiverEditorImpl<S> editor) {

		final RfReceiverImpl<S> editedReceiver = editor.getRfReceiver();
		final RfReceiverImpl<S> receiver;

		if (editedReceiver != null) {
			receiver = editedReceiver;
		} else {
			receiver = new RfReceiverImpl<>(this, editor.getRfProvider());
		}

		final RfReceiverState<S> prevState = receiver.update(editor);

		if (editedReceiver == null) {
			createReceiver(receiver);
		}

		updateReceiver(receiver, prevState);

		return receiver;
	}

	private <S extends RfSettings> void createReceiver(
			final RfReceiverImpl<S> receiver) {
		this.receivers.put(receiver.getId(), receiver);
		registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCompletion(int status) {
				if (status == STATUS_COMMITTED) {
					return;
				}
				RfStoreImpl.this.receivers.remove(receiver.getId());
			}
		});
	}

	private <S extends RfSettings> void updateReceiver(
			final RfReceiverImpl<S> receiver,
			final RfReceiverState<S> prevState) {
		registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCompletion(int status) {
				if (status == STATUS_COMMITTED) {
					return;
				}
				receiver.update(prevState);
			}

		});
	}

	void deleteReceiver(RfReceiverImpl<?> receiver) {
		this.receivers.remove(receiver.getId());
	}

	@PostConstruct
	private void loadAll() {
		this.loaded = true;
		loadReceivers();
	}

	private ConcurrentHashMap<String, RfProvider<?>> providersById() {
		if (this.providers != null) {
			return this.providers;
		}

		final ConcurrentHashMap<String, RfProvider<?>> providersById =
				new ConcurrentHashMap<>(this.providerList.size());

		for (RfProvider<?> provider : this.providerList) {
			providersById.put(provider.getId(), provider);
		}

		return this.providers = providersById;
	}

	@Transactional(readOnly = true)
	private void loadReceivers() {

		final Query query = this.entityManager.createNamedQuery("allReceivers");
		@SuppressWarnings("unchecked")
		final List<RfReceiverData> results = query.getResultList();

		for (RfReceiverData data : results) {

			final Integer id = data.getId();

			if (this.receivers.contains(id)) {
				continue;
			}

			final RfReceiverImpl<?> receiver = rfReceiver(this, data);

			if (receiver != null) {
				this.receivers.put(id, receiver);
			}
		}
	}

}
