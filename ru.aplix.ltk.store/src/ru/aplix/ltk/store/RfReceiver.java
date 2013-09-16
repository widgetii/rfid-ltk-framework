package ru.aplix.ltk.store;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.source.RfStatusMessage;


public interface RfReceiver<S extends RfSettings> {

	int getId();

	RfProvider<S> getRfProvider();

	boolean isActive();

	RfCollector getRfCollector();

	RfStatusMessage getLastStatus();

	RfReceiverEditor<S> modify();

	void delete();

}