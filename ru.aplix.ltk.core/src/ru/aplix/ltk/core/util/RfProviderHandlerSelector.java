package ru.aplix.ltk.core.util;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


/**
 * A most specific handler selector for the given RFID provider.
 *
 * <p>A selection is made based on the {@link RfProvider#getSettingsType() RFID
 * settings} classes supported by {@link #supportedSettingsClass(Object)
 * supported} by handlers. The more specific handler is the one supporting the
 * most specific settings class.</p>
 *
 * @param <H> handlers type.
 */
public abstract class RfProviderHandlerSelector<H>
		extends HandlerSelector<RfProvider<?>, H> {

	@Override
	public boolean matchingHandler(RfProvider<?> target, H handler) {

		final Class<? extends RfSettings> supported =
				supportedSettingsClass(handler);

		return supported.isAssignableFrom(target.getSettingsType());
	}

	@Override
	public H mostSpecificHandler(RfProvider<?> target, H handler1, H handler2) {

		final Class<? extends RfSettings> supported1 =
				supportedSettingsClass(handler1);
		final Class<? extends RfSettings> supported2 =
				supportedSettingsClass(handler2);

		final boolean h1 = supported2.isAssignableFrom(supported1);
		final boolean h2 = supported1.isAssignableFrom(supported2);

		if (h1) {
			if (h2) {
				return null;
			}
			return handler1;
		}
		if (h2) {
			return handler2;
		}

		return null;
	}

	/**
	 * RFID settings class supported by the given handler.
	 *
	 * @param handler target handler.
	 *
	 * @return supported settings class.
	 */
	public abstract Class<? extends RfSettings> supportedSettingsClass(
			H handler);

}
