package ru.aplix.ltk.ui;

import ru.aplix.ltk.core.RfProvider;


/**
 *
 * RFID connector's user interface context.
 *
 * <p>An instance of this context is provided by GUI application by passing it
 * to the {@link RfProviderUI#newConnectorUI(RfConnectorUIContext)} method.</p>
 *
 * @param <P> supported RFID provider interface.
 */
public interface RfConnectorUIContext<P extends RfProvider> {

	/**
	 * Returns an RFID provider the UI is created for.
	 *
	 * @return RFID provider instance.
	 */
	P getRfProvider();

}
