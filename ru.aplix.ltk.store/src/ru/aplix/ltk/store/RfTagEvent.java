package ru.aplix.ltk.store;

import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;


/**
 * RFID tag appearance event stored by some receiver.
 *
 * <p>Contains a receiver instance in addition to the RFID tag appearance
 * message data.</p>
 */
public interface RfTagEvent extends RfTagAppearanceMessage {

	/**
	 * An identifier of RFID receiver that stored this event.
	 *
	 * @return receiver identifier.
	 */
	int getReceiverId();

	/**
	 * RFID receiver that storing this event.
	 *
	 * @return receiver instance, or <code>null</code> if receiver already
	 * deleted.
	 */
	RfReceiver<?> getReceiver();

}
