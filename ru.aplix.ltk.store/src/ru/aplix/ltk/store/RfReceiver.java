package ru.aplix.ltk.store;

import java.util.List;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfStatusMessage;


/**
 * RFID receiver.
 *
 * <p>Each receiver is responsible for obtaining RFID data from particular
 * source. When {@link #isActive() active}, a receiver stores RFID data receive
 * in database.</p>
 *
 * <p>Receivers are persistent. Their data is stored in database.</p>
 *
 * <p>A receiver can be modified by its {@link RfReceiverEditor editor},
 * returned from {@link #modify()} method. New receivers can be created using
 * {@link RfStore#newRfReceiver(RfProvider)} method.</p>
 *
 * @param <S> RFID settings type.
 */
public interface RfReceiver<S extends RfSettings> {

	/**
	 * RFID receiver identifier.
	 *
	 * @return unique identifier of the receiver.
	 */
	int getId();

	/**
	 * RFID provider this receiver is  created for.
	 *
	 * @return the provider passed to {@link RfStore#newRfReceiver(RfProvider)}
	 * method.
	 */
	RfProvider<S> getRfProvider();

	/**
	 * Whether this receiver is active.
	 *
	 * @return <code>true</code> when receiver is active, or <code>false</code>
	 * otherwise.
	 */
	boolean isActive();

	/**
	 * RFID collector this receiver is receiving data from.
	 *
	 * @return RFID collector, or <code>null</code> if this receiver is not
	 * active.
	 */
	RfCollector getRfCollector();

	/**
	 * Last status received from {@link #getRfCollector() collector}.
	 *
	 * @return RFID status, or <code>null</code> if this receiver is not active.
	 */
	RfStatusMessage getLastStatus();

	/**
	 * Loads RFID tag appearance events stored by this receiver.
	 *
	 * @param fromEventId first {@link RfTagAppearanceMessage#getEventId() event
	 * identifier} to load.
	 * @param limit maximum number of events to load.
	 *
	 * @return tag appearance messages ordered by their identifier, starting
	 * from the given one.
	 */
	List<? extends RfTagAppearanceMessage> loadEvents(
			long fromEventId,
			int limit);

	/**
	 * Modifies this receiver.
	 *
	 * <p>Returns an editor of this receiver. All modifications made in that
	 * editor will be applied when {@link RfReceiverEditor#save() saved}.</p>
	 *
	 * @return an editor of this receiver.
	 */
	RfReceiverEditor<S> modify();

	/**
	 * Deletes this receiver.
	 *
	 * @param deleteTags <code>true</code> to delete all tag appearance events
	 * stored by this receiver, or <code>false</code> to preserve them.
	 */
	void delete(boolean deleteTags);

}
