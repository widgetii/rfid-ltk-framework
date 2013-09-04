package ru.aplix.ltk.ui;

import javax.swing.JComponent;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


/**
 * User interface for RFID settings.
 *
 * <p>An instance creation is requested by GUI applications by calling
 * {@link RfProviderUI#newSettingsUI(RfSettingsUIContext) method}.</p>
 *
 * @param <S> supported RFID settings type.
 */
public interface RfSettingsUI<S extends RfSettings> {

	/**
	 * A Swing component containing RFID settings.
	 *
	 * @return settings component to display to the user, or <code>null</code>
	 * if settings UI is not required.
	 */
	JComponent getUIComponent();

	/**
	 * Builds RFID settings.
	 *
	 * <p>This method is responsible for creating a settings instance (by
	 * calling {@link RfProvider#newSettings()} method}, and configuring it with
	 * user input.</p>
	 *
	 * @return configured RFID settings.
	 */
	S buildSettings();

}
