package ru.aplix.ltk.ui;


/**
 *
 * RFID connector's user interface context.
 *
 * <p>An instance of this context is provided by GUI application by passing it
 * to the {@link RfProviderUI#newConnectorUI(RfConnectorUIContext)} method.</p>
 *
 * @param <P> supported RFID provider interface.
 */
public interface RfConnectorUIContext<P> {

	/**
	 * Returns an RFID provider the UI is created for.
	 *
	 * @return RFID provider instance.
	 */
	P getRfProvider();

}
