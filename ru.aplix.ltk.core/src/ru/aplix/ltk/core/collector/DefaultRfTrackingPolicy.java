package ru.aplix.ltk.core.collector;

import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
import ru.aplix.ltk.core.util.Parameter;
import ru.aplix.ltk.core.util.Parameters;


/**
 * Default RFID tags tracking policy.
 *
 * <p>It provides trackers of type {@link DefaultRfTracker} with specified
 * settings.</p>
 */
public class DefaultRfTrackingPolicy implements RfTrackingPolicy {

	/**
	 * Transaction timeout parameter.
	 *
	 * @see #getTransactionTimeout()
	 */
	public static final Parameter<Long> RF_TRANSACTION_TIMEOUT =
			LONG_PARAMETER_TYPE.parameter("transactionTimeout")
			.byDefault(4000L);

	/**
	 * Invalidation timeout parameter.
	 *
	 * @see #getInvalidationTimeout()
	 */
	public static final Parameter<Long> RF_INVALIDATION_TIMEOUT =
			LONG_PARAMETER_TYPE.parameter("invalidationParameter")
			.byDefault(8000L);

	private long transactionTimeout = RF_TRANSACTION_TIMEOUT.getDefault();
	private long invalidationTimeout = RF_INVALIDATION_TIMEOUT.getDefault();

	/**
	 * Transaction timeout.
	 *
	 * @return timeout value in milliseconds.
	 */
	public final long getTransactionTimeout() {
		return this.transactionTimeout;
	}

	/**
	 * Changes transaction timeout.
	 *
	 * @param transactionTimeout new timeout value in milliseconds, or
	 * non-positive value to set it to the default value.
	 */
	public void setTransactionTimeout(long transactionTimeout) {
		this.transactionTimeout =
				transactionTimeout > 0
				? transactionTimeout : RF_TRANSACTION_TIMEOUT.getDefault();
	}

	/**
	 * Cache invalidation timeout.
	 *
	 * @return timeout value in milliseconds.
	 */
	public final long getInvalidationTimeout() {
		return this.invalidationTimeout;
	}

	/**
	 * Changes cache invalidation timeout.
	 *
	 * <p>Set it to zero to invalidate the unread tags immediately after the
	 * transaction end.</p>
	 *
	 * @param invalidationTimeout new timeout value in milliseconds, or negative
	 * value to set it to the default value.
	 */
	public final void setInvalidationTimeout(long invalidationTimeout) {
		this.invalidationTimeout =
				invalidationTimeout >= 0
				? invalidationTimeout : RF_INVALIDATION_TIMEOUT.getDefault();
	}

	@Override
	public RfTracker newTracker(RfCollector collector) {

		final DefaultRfTracker tracker = new DefaultRfTracker();

		tracker.setTransactionTimeout(getTransactionTimeout());
		tracker.setInvalidationTimeout(getInvalidationTimeout());

		return tracker;
	}

	@Override
	public void read(Parameters params) {
		setTransactionTimeout(params.valueOf(
				RF_TRANSACTION_TIMEOUT,
				getTransactionTimeout()));
		setInvalidationTimeout(params.valueOf(
				RF_INVALIDATION_TIMEOUT,
				getInvalidationTimeout()));
	}

	@Override
	public void write(Parameters params) {
		params.set(RF_TRANSACTION_TIMEOUT, getTransactionTimeout());
		params.set(RF_INVALIDATION_TIMEOUT, getInvalidationTimeout());
	}

}