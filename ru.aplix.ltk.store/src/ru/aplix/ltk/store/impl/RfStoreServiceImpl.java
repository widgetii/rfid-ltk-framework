package ru.aplix.ltk.store.impl;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfStoreService;


@Component("rfStoreService")
public class RfStoreServiceImpl implements RfStoreService {

	private final AtomicInteger idSeq = new AtomicInteger();
	private final ConcurrentHashMap<Integer, RfStoreImpl<?>> stores =
			new ConcurrentHashMap<>();

	@Override
	public Collection<? extends RfStoreImpl<?>> allRfStores() {
		return this.stores.values();
	}

	@Override
	public RfStore<?> rfStoreById(int id) {
		return this.stores.get(id);
	}

	@Override
	public <S extends RfSettings> RfStore<S> newRfStore(
			RfProvider<S> provider) {
		return new RfStoreImpl<>(this, this.idSeq.incrementAndGet(), provider);
	}

	void saveStore(RfStoreImpl<?> store) {
		this.stores.put(store.getId(), store);
	}

	void deleteStore(RfStoreImpl<?> store) {
		this.stores.remove(store.getId());
	}

}
