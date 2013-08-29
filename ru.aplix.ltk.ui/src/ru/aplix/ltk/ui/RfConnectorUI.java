package ru.aplix.ltk.ui;

import java.awt.Component;

import ru.aplix.ltk.core.RfConnector;
import ru.aplix.ltk.core.RfProvider;


/**
 * User interface for RFID connector.
 *
 * <p>An instance creation is requested by GUI applications by calling
 * {@link RfProviderUI#newConnectorUI(RfConnectorUIContext) method}.</p>
 */
public interface RfConnectorUI {

	/**
	 * An AWT component containing RFID connection settings.
	 *
	 * @return connection settings component to display to the user, or
	 * <code>null</code> if the settings UI is not required.
	 */
	Component getSettingsUI();

	/**
	 * Builds a new RFID connector.
	 *
	 * <p>This method is responsible for creating a connector (by calling
	 * {@link RfProvider#newConnector()} method}, and configuring it with
	 * user input.</p>
	 *
	 * @return new configured RFID connector instance.
	 */
	RfConnector buildConnector();

}
