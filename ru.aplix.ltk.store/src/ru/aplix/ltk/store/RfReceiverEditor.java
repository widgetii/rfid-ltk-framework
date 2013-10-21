package ru.aplix.ltk.store;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


/**
 * RFID receiver editor.
 *
 * <p>It is used to modify RFID receiver's properties. All modifications will be
 * applied to target receiver after {@link #save() saving}.</p>
 *
 * @param <S> RFID settings type.
 */
public interface RfReceiverEditor<S extends RfSettings> {

	/**
	 * RFID provider the modified receiver is created for.
	 *
	 * @return the provider passed to {@link RfStore#newRfReceiver(RfProvider)}
	 * method.
	 */
	RfProvider<S> getRfProvider();

	/**
	 * RFID settings of target receiver.
	 *
	 * @return RFID settings, which will be applied to target receiver after
	 * {@link #save() saving}.
	 */
	S getRfSettings();

	/**
	 * Sets RFID settings of target receiver.
	 *
	 * @param settings new RFID settings.
	 */
	void setRfSettings(S settings);

	/**
	 * Whether the target receiver is active.
	 *
	 * @return <code>true</code> if target receiver will be active after
	 * {@link #save() saving}, or <code>false</code> to make it inactive.
	 */
	boolean isActive();

	/**
	 * Sets the target receiver activity.
	 *
	 * @param active <code>true</code> to make the target receiver active,
	 * or <code>false</code> to make it inactive.
	 */
	void setActive(boolean active);

	/**
	 * Applies all modifications made by to target receiver, and saves them in
	 * database.
	 *
	 * <p>If this editor was created by {@link RfStore#newRfReceiver(
	 * RfProvider)} method call, then this method call creates a new receiver.
	 * </p>
	 *
	 * @return updated RFID receiver.
	 */
	RfReceiver<S> save();

}
