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
	public <S extends RfSettings> RfStoreEditorImpl<S> newRfStore(
			RfProvider<S> provider) {
		return new RfStoreEditorImpl<>(this, provider);
	}

	<S extends RfSettings> RfStoreImpl<S> saveStore(
			RfStoreEditorImpl<S> editor) {

		final RfStoreImpl<S> editedStore = editor.getStore();
		final RfStoreImpl<S> store;

		if (editedStore != null) {
			store = editedStore;
		} else {
			store = new RfStoreImpl<>(
					this,
					this.idSeq.incrementAndGet(),
					editor.getRfProvider());
		}

		store.update(editor);

		if (editedStore == null) {
			this.stores.put(store.getId(), store);
		}

		return store;
	}

	void deleteStore(RfStoreImpl<?> store) {
		this.stores.remove(store.getId());
	}

}
