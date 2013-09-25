package ru.aplix.ltk.collector.log;

import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
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
			LONG_PARAMETER_TYPE.parameter(RF_LOG_PREFIX + ".size")
			.byDefault(33554432L); // 1 GiB

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
