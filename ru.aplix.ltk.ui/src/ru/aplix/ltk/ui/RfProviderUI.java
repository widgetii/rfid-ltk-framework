package ru.aplix.ltk.ui;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.util.RfProviderHandlerSelector;
import ru.aplix.ltk.ui.impl.DefaultRfProviderUI;
import ru.aplix.ltk.ui.impl.RfProviderUISelector;


/**
 * User interface for RFID provider.
 *
 * <p>Implementations expected to be registered as OSGi services. The UI most
 * specific for any given RFID provider will be used by GUI applications.</p>
 *
 * @param <P> supported RFID provider interface.
 */
public interface RfProviderUI<P extends RfProvider> {

	/**
	 * Default RFID provider user interface.
	 */
	RfProviderUI<RfProvider> DEFAULT_RF_PROVIDER_UI =
			new DefaultRfProviderUI();

	/**
	 * User interface selector for UI provider.
	 *
	 * <p>Can be used to select the most specific UI for the given RFID
	 * providers.</p>
	 */
	RfProviderHandlerSelector<RfProviderUI<?>> RF_PROVIDER_UI_SELECTOR =
			new RfProviderUISelector();

	/**
	 * Returns RFID provider interface supported by this UI.
	 *
	 * @return provider interface class.
	 */
	Class<? extends P> getProviderInterface();

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
	RfConnectorUI newConnectorUI(RfConnectorUIContext<P> context);

}
