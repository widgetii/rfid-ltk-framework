package ru.aplix.ltk.core.collector;

import static ru.aplix.ltk.core.collector.DefaultRfTracker.DEFAULT_INVALIDATION_TIMEOUT;
import static ru.aplix.ltk.core.collector.DefaultRfTracker.DEFAULT_TRANSACTION_TIMEOUT;
import ru.aplix.ltk.core.util.Parameters;


/**
 * Default RFID tags tracking policy.
 *
 * <p>It provides trackers of type {@link DefaultRfTracker} with specified
 * settings.</p>
 */
public class DefaultRfTrackingPolicy implements RfTrackingPolicy {

	private long transactionTimeout = DEFAULT_TRANSACTION_TIMEOUT;
	private long invalidationTimeout = DEFAULT_INVALIDATION_TIMEOUT;

	public final long getTransactionTimeout() {
		return this.transactionTimeout;
	}

	public void setTransactionTimeout(long transactionTimeout) {
		this.transactionTimeout =
				transactionTimeout > 0
				? transactionTimeout : DEFAULT_TRANSACTION_TIMEOUT;
	}

	public final long getInvalidationTimeout() {
		return this.invalidationTimeout;
	}

	public final void setInvalidationTimeout(long invalidationTimeout) {
		this.invalidationTimeout = invalidationTimeout;
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
		setTransactionTimeout(params.longValueOf(
				"transactionTimeout",
				DEFAULT_TRANSACTION_TIMEOUT,
				getTransactionTimeout()));
		setInvalidationTimeout(params.longValueOf(
				"invalidationTimeout",
				DEFAULT_INVALIDATION_TIMEOUT,
				getInvalidationTimeout()));
	}

	@Override
	public void write(Parameters params) {
		params.set("transactionTimeout", getTransactionTimeout());
		params.set("invalidationTimeout", getInvalidationTimeout());
	}

}