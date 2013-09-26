package ru.aplix.ltk.collector.log;

import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.STRING_PARAMETER_TYPE;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.util.Parameter;


/**
 * Collector log constants.
 */
public final class RfLogConstants {

	/**
	 * The prefix of all collector log properties.
	 */
	public static final String RF_LOG_PREFIX = "ru.aplix.ltk.collector.log";

	/**
	 * Collector log size property. 1 GiB by default.
	 */
	public static final Parameter<Long> RF_LOG_SIZE =
			LONG_PARAMETER_TYPE.parameter("size")
			.byDefault(33554432L);

	/**
	 * OSGI service filter property to select RFID providers to create logging
	 * collectors for.
	 */
	public static final Parameter<String> RF_LOG_FILTER =
			STRING_PARAMETER_TYPE.parameter("filter");

	/**
	 * A proxy identifier created by logging RFID provider.
	 *
	 * <p>This value placed to the {@link RfProvider#RF_PROVIDER_PROXY} property
	 * of the service.</p>
	 */
	public static final String RF_LOG_PROXY_ID = "log";

	/**
	 * A logging RFID provider suffix.
	 *
	 * <p>This suffix is added to the original provider identifier, when
	 * creating a logging one.</p>
	 */
	public static final String RF_LOG_PROVIDER_ID_SUFFIX = "-log";

	private RfLogConstants() {
	}

}
