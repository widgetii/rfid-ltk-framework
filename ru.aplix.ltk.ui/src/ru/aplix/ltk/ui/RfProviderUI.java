package ru.aplix.ltk.ui;


/**
 * User interface for RFID provider.
 *
 * <p>Implementations expected to be registered as OSGi services. The UI most
 * specific for any given RFID provider will be used by GUI applications.</p>
 *
 * @param <P> supported RFID provider interface.
 */
public interface RfProviderUI<P> {

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
