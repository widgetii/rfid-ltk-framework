package ru.aplix.ltk.store.impl;

import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;
import static ru.aplix.ltk.store.impl.RfReceiverImpl.rfReceiver;
import static ru.aplix.ltk.store.impl.monitor.RfStoreMtrTarget.RF_STORE_MTR_TARGET;

import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.*;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.monitor.Monitor;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfTagQuery;
import ru.aplix.ltk.store.impl.monitor.RfStoreMtr;
import ru.aplix.ltk.store.impl.persist.RfReceiverData;
import ru.aplix.ltk.store.impl.persist.RfTagEventData;


@Component("rfStore")
public class RfStoreImpl
		implements RfStore,
		ApplicationContextAware,
		DisposableBean,
		ApplicationListener<ContextRefreshedEvent> {

	private Monitor monitor;
	private RfStoreMtr monitoring;
	@PersistenceContext(unitName = "rfstore")
	private EntityManager entityManager;
	private final ExecutorService executor =
			Executors.newSingleThreadExecutor();
	private final ConcurrentHashMap<Integer, RfReceiverImpl<?>> allReceivers =
			new ConcurrentHashMap<>();
	private final
	ConcurrentHashMap<String, RfProviderReceivers> providerReceivers =
			new ConcurrentHashMap<>();
	private final AtomicInteger loaded = new AtomicInteger();

	public final ExecutorService getExecutor() {
		return this.executor;
	}

	public final Monitor getMonitor() {
		return this.monitor;
	}

	public final RfStoreMtr getMonitoring() {
		return this.monitoring;
	}

	@Override
	public Collection<? extends RfReceiverImpl<?>> allRfReceivers() {
		return this.allReceivers.values();
	}

	@Override
	public RfReceiver<?> rfReceiverById(int id) {
		loadReceivers();
		return this.allReceivers.get(id);
	}

	@Override
	public <S extends RfSettings> RfReceiverEditorImpl<S> newRfReceiver(
			RfProvider<S> provider) {
		return new RfReceiverEditorImpl<>(this, provider);
	}

	@Override
	public RfTagQuery tagQuery() {
		return new RfTagQueryImpl(this);
	}

	@Override
	public void setApplicationContext(
			ApplicationContext applicationContext)
	throws BeansException {
		if (!(applicationContext instanceof ConfigurableApplicationContext)) {
			return;
		}

		final ConfigurableApplicationContext context =
				(ConfigurableApplicationContext) applicationContext;

		context.addApplicationListener(this);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		loadAll();
	}

	public void providerAdded(
			RfProvider<?> provider,
			@SuppressWarnings("unused") Dictionary<?, ?> properties) {

		final String id = provider.getId();

		if (this.allReceivers.containsKey(id)) {
			return;
		}

		final RfProviderReceivers providerReceivers =
				new RfProviderReceivers(this, provider);

		this.providerReceivers.put(id, providerReceivers);
		if (this.loaded.get() > 0) {
			providerReceivers.load();
		}
	}

	public void providerRemoved(
			RfProvider<?> provider,
			@SuppressWarnings("unused") Dictionary<?, ?> properties) {

		final RfProviderReceivers receivers =
				this.providerReceivers.remove(provider.getId());

		if (receivers != null && this.loaded.get() > 0) {
			receivers.shutdown();
		}
	}

	@Override
	public void destroy() throws Exception {
		this.loaded.set(-1);
		shutdown();
	}

	final EntityManager getEntityManager() {
		return this.entityManager;
	}

	@SuppressWarnings("unchecked")
	final <S extends RfSettings> RfProvider<S> providerById(
			String providerId) {

		final RfProviderReceivers receivers =
				this.providerReceivers.get(providerId);

		if (receivers == null) {
			return null;
		}

		return (RfProvider<S>) receivers.getRfProvider();
	}

	@Transactional
	void deleteReceiver(
			final RfReceiverImpl<?> receiver,
			boolean deleteTags) {
		removeReceiver(receiver);

		final Query deleteReceiver =
				getEntityManager().createNamedQuery("deleteRfReceiver");

		deleteReceiver.setParameter("id", receiver.getId());
		deleteReceiver.executeUpdate();

		if (deleteTags) {

			final Query deleteEvents =
					getEntityManager().createNamedQuery("deleteRfTagEvents");

			deleteEvents.setParameter("receiverId", receiver.getId());
			deleteEvents.executeUpdate();
		}

		registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCompletion(int status) {
				if (status != STATUS_COMMITTED) {
					addReceiver(receiver);
				}
			}
		});
	}

	@Transactional
	<S extends RfSettings> RfReceiverImpl<S> saveReceiver(
			RfReceiverEditorImpl<S> editor) {

		final RfReceiverImpl<S> editedReceiver = editor.getRfReceiver();
		final RfReceiverImpl<S> receiver;

		if (editedReceiver != null) {
			editedReceiver.getMonitoring().update();
			receiver = editedReceiver;
		} else {
			getMonitoring().createReceiver();
			receiver = new RfReceiverImpl<>(this, editor.getRfProvider());
		}

		final RfReceiverState<S> prevState = receiver.update(editor);

		if (editedReceiver == null) {
			createReceiver(receiver);
		} else {
			updateReceiver(receiver, prevState);
		}

		return receiver;
	}

	@Transactional
	void saveEvent(RfTagEventData event) {
		if (event.isInitialEvent()) {
			hideVisibleTags(event);
		}
		if (!eventExists(event)) {
			getEntityManager().persist(event);
		}
	}

	private void hideVisibleTags(RfTagEventData event) {

		final Query query = getEntityManager().createNativeQuery(
				"SELECT rfstore.hide_visible_tags(?, ?, ?)");

		query.setParameter(1, event.getReceiverId());
		query.setParameter(2, event.getEventId());
		query.setParameter(3, event.getSqlTimestamp());

		query.getSingleResult();
	}

	private boolean eventExists(RfTagEventData event) {

		final long eventId = event.getEventId();

		if (eventId <= 0) {
			return false;
		}

		final Query query =
				getEntityManager().createNamedQuery("rfTagEventById");

		query.setParameter("receiverId", event.getReceiverId());
		query.setParameter("eventId", eventId);

		return !query.getResultList().isEmpty();
	}

	@Transactional
	long lastEventId(RfReceiverImpl<?> receiver) {

		final Query query =
				getEntityManager().createNamedQuery("lastRfTagEventId");

		query.setParameter("receiverId", receiver.getId());

		final Long result = (Long) query.getSingleResult();

		return result != null ? result.longValue() : 0L;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	List<RfTagEventData> allReceiverEvents(RfReceiverImpl<?> receiver) {

		final Query query =
				getEntityManager().createNamedQuery("allReceiverRfTagEvents");

		query.setParameter("receiverId", receiver.getId());

		return query.getResultList();
	}

	@Transactional
	List<RfTagEventData> findEvents(
			RfTagQueryImpl query,
			TypedQuery<Long> totalsQuery,
			TypedQuery<RfTagEventData> listQuery) {
		query.setTotalCount(totalsQuery.getSingleResult());
		return listQuery.getResultList();
	}

	final void addReceivers(List<RfReceiverData> receiversData) {
		for (RfReceiverData data : receiversData) {

			final RfReceiverImpl<?> receiver = rfReceiver(this, data);

			if (receiver != null) {
				addReceiver(receiver);
			}
		}
	}

	final RfReceiverImpl<?> removeReceiver(Integer receiverId) {
		return this.allReceivers.remove(receiverId);
	}

	@Autowired
	private void startMonitoring(Monitor monitor) {
		this.monitor = monitor;
		this.monitoring = monitor.monitoringOf(RF_STORE_MTR_TARGET);
	}

	@Transactional(readOnly = true)
	void requestProviderReceivers(RfProvider<?> provider) {

		final Query query =
				getEntityManager().createNamedQuery("providerRfReceivers");

		query.setParameter("providerId", provider.getId());

		@SuppressWarnings("unchecked")
		final List<RfReceiverData> receiversData = query.getResultList();

		addReceivers(receiversData);
	}

	private <S extends RfSettings> void createReceiver(
			final RfReceiverImpl<S> receiver) {
		this.allReceivers.put(receiver.getId(), receiver);
		registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCompletion(int status) {
				if (status == STATUS_COMMITTED) {
					return;
				}
				RfStoreImpl.this.allReceivers.remove(receiver.getId());
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

	private void loadAll() {
		getExecutor().submit(new Runnable() {
			@Override
			public void run() {
				loadReceivers();
			}
		});
	}

	private void shutdown() {
		getMonitoring().shutdown();
		for (RfProviderReceivers receivers : this.providerReceivers.values()) {
			receivers.shutdown();
		}
		this.executor.shutdown();
	}

	private void loadReceivers() {
		if (this.loaded.get() != 0) {
			return;
		}
		try {
			getMonitoring().loadReceivers("Load receivers");
			requestReceivers();
			this.loaded.compareAndSet(0, 1);
		} catch (Throwable e) {
			getMonitoring().failedToLoadReceivers(
					"Failed to load receivers",
					e);
			throw e;
		}
	}

	@Transactional(readOnly = true)
	private void requestReceivers() {

		final Query query =
				this.entityManager.createNamedQuery("allRfReceivers");
		@SuppressWarnings("unchecked")
		final List<RfReceiverData> receiversData = query.getResultList();

		addReceivers(receiversData);
	}

	private void addReceiver(RfReceiverImpl<?> receiver) {
		this.allReceivers.put(receiver.getId(), receiver);

		final RfProviderReceivers providerReceivers =
				this.providerReceivers.get(receiver.getRfProvider().getId());

		if (providerReceivers != null) {
			providerReceivers.add(receiver);
		}
	}

	private void removeReceiver(RfReceiverImpl<?> receiver) {
		removeReceiver(receiver.getId());

		final RfProviderReceivers providerReceivers =
				this.providerReceivers.get(receiver.getRfProvider().getId());

		if (providerReceivers != null) {
			providerReceivers.remove(receiver);
		}
	}

}
