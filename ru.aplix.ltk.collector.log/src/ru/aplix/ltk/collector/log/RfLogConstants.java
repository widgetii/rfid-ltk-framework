package ru.aplix.ltk.collector.log;

import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.BOOLEAN_PARAMETER_TYPE;
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
	 * Collector log directory.
	 *
	 * <p>If not specified, then will be created in plug-in storage area.</p>
	 */
	public static final Parameter<String> RF_LOG_DIR =
			STRING_PARAMETER_TYPE.parameter("dir")
			.byDefault("");

	/**
	 * Collector log size property. 1 GiB by default.
	 */
	public static final Parameter<Long> RF_LOG_SIZE =
			LONG_PARAMETER_TYPE.parameter("size")
			.byDefault(33554432L);

	/**
	 * Collector log file sync mode parameter. Disabled by default.
	 */
	public static final Parameter<Boolean> RF_LOG_FSYNC =
			BOOLEAN_PARAMETER_TYPE.parameter("fsync")
			.byDefault(false);

	/**
	 * Collector log file meta-data sync mode parameter. Disabled by default.
	 */
	public static final Parameter<Boolean> RF_LOG_FSYNC_META =
			BOOLEAN_PARAMETER_TYPE.parameter("fsyncMeta")
			.byDefault(false);

	/**
	 * Parameter indicating whether the tag appearance messages should be
	 * logged. True by default.
	 */
	public static final Parameter<Boolean> RF_LOG_APPEARANCE =
			BOOLEAN_PARAMETER_TYPE.parameter("logAppearance")
			.byDefault(true);

	/**
	 * Parameter indicating whether the tag disappearance messages should be
	 * logged. True by default.
	 */
	public static final Parameter<Boolean> RF_LOG_DISAPPEARANCE =
			BOOLEAN_PARAMETER_TYPE.parameter("logDisappearance")
			.byDefault(true);

	/**
	 * OSGi service filter property to select RFID providers to create logging
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
