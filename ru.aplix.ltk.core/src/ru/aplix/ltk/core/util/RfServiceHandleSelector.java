package ru.aplix.ltk.core.util;

import ru.aplix.ltk.core.RfService;


/**
 * A most specific handler selector for the given RFID OSGi service.
 *
 * <p>A selection is made based on the service classes
 * {@link #supportedServiceClass(Object) supported} by handlers. The more
 * specific handler is the one supporting the most specific service class.</p>
 *
 * @param <H> handlers type.
 */
public abstract class RfServiceHandleSelector<H>
		extends HandlerSelector<RfService, H> {

	@Override
	public boolean matchingHandler(RfService target, H handler) {

		final Class<? extends RfService> supported =
				supportedServiceClass(handler);

		return supported.isInstance(target);
	}

	@Override
	public H mostSpecificHandler(RfService target, H handler1, H handler2) {

		final Class<? extends RfService> supported1 =
				supportedServiceClass(handler1);
		final Class<? extends RfService> supported2 =
				supportedServiceClass(handler2);

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
	 * Service class supported by the given handler.
	 *
	 * @param handler target handler.
	 *
	 * @return supported service class.
	 */
	public abstract Class<? extends RfService> supportedServiceClass(H handler);

}
