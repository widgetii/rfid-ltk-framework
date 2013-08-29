package ru.aplix.ltk.core.util;

import ru.aplix.ltk.core.RfProvider;


/**
 * A most specific handler selector for the given RFID provider.
 *
 * <p>A selection is made based on provider classes
 * {@link #supportedProviderClass(Object) supported} by handlers. The more
 * specific handler is the one supporting the most specific provider class.</p>
 *
 * @param <H> handlers type.
 */
public abstract class RfProviderHandlerSelector<H>
		extends HandlerSelector<RfProvider, H> {

	@Override
	public boolean matchingHandler(RfProvider target, H handler) {

		final Class<? extends RfProvider> supported =
				supportedProviderClass(handler);

		return supported.isInstance(target);
	}

	@Override
	public H mostSpecificHandler(RfProvider target, H handler1, H handler2) {

		final Class<? extends RfProvider> supported1 =
				supportedProviderClass(handler1);
		final Class<? extends RfProvider> supported2 =
				supportedProviderClass(handler2);

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
	 * Provider class supported by the given handler.
	 *
	 * @param handler target handler.
	 *
	 * @return supported provider class.
	 */
	public abstract Class<? extends RfProvider> supportedProviderClass(
			H handler);

}
