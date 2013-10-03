package ru.aplix.ltk.store.impl;

import static org.osgi.service.log.LogService.LOG_ERROR;
import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;
import static ru.aplix.ltk.store.impl.RfReceiverImpl.rfReceiver;

import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.osgi.service.log.LogService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.impl.persist.RfReceiverData;


@Component("rfStore")
public class RfStoreImpl
		implements RfStore,
		ApplicationContextAware,
		DisposableBean,
		ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	@Qualifier("log")
	private LogService log;
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

	public final ExecutorService getExecutor() {
		return this.executor;
	}

	public final LogService log() {
		return this.log;
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

		return (RfProvider<S>) receivers.getProvider();
	}

	@Transactional
	void deleteReceiver(final RfReceiverImpl<?> receiver) {
		removeReceiver(receiver);

		final Query query =
				getEntityManager().createNamedQuery("deleteRfReceiver");

		query.setParameter("id", receiver.getId());
		query.executeUpdate();

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
			receiver = editedReceiver;
		} else {
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

	@Transactional(readOnly = true)
	void loadProviderReceivers(RfProvider<?> provider) {

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
				try {
					loadReceivers();
				} catch (Throwable e) {
					log().log(LOG_ERROR, "Failed to load receivers", e);
				}
			}
		});
	}

	private void shutdown() {
		for (RfProviderReceivers receivers : this.providerReceivers.values()) {
			receivers.shutdown();
		}
		this.executor.shutdown();
	}

	@Transactional(readOnly = true)
	private void loadReceivers() {
		if (this.loaded.get() != 0) {
			return;
		}

		final Query query =
				this.entityManager.createNamedQuery("allRfReceivers");
		@SuppressWarnings("unchecked")
		final List<RfReceiverData> receiversData = query.getResultList();

		addReceivers(receiversData);

		this.loaded.compareAndSet(0, 1);
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
