package ru.aplix.ltk.store;

import java.util.Collection;

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
	 * Creates new RFID tag query to search for stored tag events.
	 *
	 * @return new tag query.
	 */
	RfTagQuery tagQuery();

}
