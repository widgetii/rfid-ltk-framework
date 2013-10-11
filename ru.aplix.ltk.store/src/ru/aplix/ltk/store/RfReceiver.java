package ru.aplix.ltk.store;

import java.util.List;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfStatusMessage;


public interface RfReceiver<S extends RfSettings> {

	int getId();

	RfProvider<S> getRfProvider();

	boolean isActive();

	RfCollector getRfCollector();

	RfStatusMessage getLastStatus();

	List<? extends RfTagAppearanceMessage> loadEvents(
			long fromEventId,
			int limit);

	RfReceiverEditor<S> modify();

	void delete(boolean deleteTags);

}
