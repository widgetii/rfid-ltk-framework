package ru.aplix.ltk.store;

import java.util.Collection;
import java.util.List;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


/**
 * RFID tags store.
 *
 * <p>A store implementation is registered as OSGi service.</p>
 */
public interface RfStore {

	/**
	 * All RFID receivers.
	 *
	 * @return a collection of all available RFID receivers.
	 */
	Collection<? extends RfReceiver<?>> allRfReceivers();

	/**
	 * Returns an RFID receiver with the given identifier.
	 *
	 * @param id an {@link RfReceiver#getId() identifier} of the receiver.
	 *
	 * @return receiver, or <code>null</code> if there is no receiver with the
	 * given identifier.
	 */
	RfReceiver<?> rfReceiverById(int id);

	/**
	 * Creates new RFID receiver.
	 *
	 * @param <S> RFID settings type.
	 * @param provider RFID provider to create receiver for.
	 *
	 * @return an editor of new RFID receiver.
	 */
	<S extends RfSettings> RfReceiverEditor<S> newRfReceiver(
			RfProvider<S> provider);

	/**
	 * Loads all RFID tag appearance events occurred since the given time,
	 * and stored by any receiver.
	 *
	 * @param timestamp UNIX time since which the events occurred.
	 * @param offset an index of the first event to load.
	 * @param limit the maximum number of events to load.
	 *
	 * @return tag appearance events ordered by their time stamps and
	 * identifiers, occurred since the given time.
	 */
	List<? extends RfTagEvent> allEventsSince(
			long timestamp,
			int offset,
			int limit);

}
