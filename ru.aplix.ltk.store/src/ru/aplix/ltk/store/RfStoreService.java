package ru.aplix.ltk.store;

import java.util.Collection;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


public interface RfStoreService {

	Collection<? extends RfStore<?>> allRfStores();

	RfStore<?> rfStoreById(int id);

	<S extends RfSettings> RfStore<S> newRfStore(RfProvider<S> provider);

}
