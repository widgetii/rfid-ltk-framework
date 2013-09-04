package ru.aplix.ltk.ui;

import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.RfProviderHandlerSelector;
import ru.aplix.ltk.ui.impl.DefaultRfProviderUI;
import ru.aplix.ltk.ui.impl.RfProviderUISelector;


/**
 * User interface for RFID provider.
 *
 * <p>Implementations expected to be registered as OSGi services. The UI most
 * specific for any given RFID provider will be used by GUI applications.</p>
 *
 * @param <S> supported RFID settings interface.
 */
public interface RfProviderUI<S extends RfSettings> {

	/**
	 * Default RFID provider user interface.
	 */
	RfProviderUI<RfSettings> DEFAULT_RF_PROVIDER_UI = new DefaultRfProviderUI();

	/**
	 * RFID UI provider class.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	Class<RfProviderUI<?>> RF_PROVIDER_UI_CLASS = (Class) RfProviderUI.class;

	/**
	 * User interface selector for UI provider.
	 *
	 * <p>Can be used to select the most specific UI for the given RFID
	 * providers.</p>
	 */
	RfProviderHandlerSelector<RfProviderUI<?>> RF_PROVIDER_UI_SELECTOR =
			new RfProviderUISelector();

	/**
	 * RFID settings type supported by this UI.
	 *
	 * @return settings class.
	 */
	Class<? extends S> getSettingsType();

	/**
	 * Creates an RFID connector UI.
	 *
	 * <p>This method is invoked by GUI applications in order to create a
	 * connection configuration UI components.</p>
	 *
	 * @param context connector's UI context.
	 *
	 * @return new connector UI placed to the given container.
	 */
	RfSettingsUI<S> newSettingsUI(RfSettingsUIContext<S> context);

}
