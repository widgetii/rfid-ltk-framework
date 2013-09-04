package ru.aplix.ltk.ui;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


/**
 * RFID settings user interface context.
 *
 * <p>An instance of this context is provided by GUI application by passing it
 * to the {@link RfProviderUI#newSettingsUI(RfSettingsUIContext)} method.</p>
 *
 * @param <S> supported RFID settings type.
 */
public interface RfSettingsUIContext<S extends RfSettings> {

	/**
	 * Returns an RFID provider the UI is created for.
	 *
	 * @return RFID provider instance.
	 */
	RfProvider<S> getRfProvider();

}
