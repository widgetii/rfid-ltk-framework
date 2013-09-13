package ru.aplix.ltk.store;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;


public interface RfStore<S extends RfSettings> {

	int getId();

	RfProvider<S> getRfProvider();

	boolean isActive();

	RfCollector getRfCollector();

	RfStoreEditor<S> modify();

	void delete();

}
