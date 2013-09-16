package ru.aplix.ltk.store;

import java.util.Collection;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


public interface RfStoreService {

	Collection<? extends RfReceiver<?>> allRfReceivers();

	RfReceiver<?> rfReceiverById(int id);

	<S extends RfSettings> RfReceiverEditor<S> newRfReceiver(
			RfProvider<S> provider);

}
