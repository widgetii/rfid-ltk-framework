package ru.aplix.ltk.core.collector;

import java.util.HashMap;
import java.util.Map;

import ru.aplix.ltk.core.reader.RfDataMessage;
import ru.aplix.ltk.core.reader.RfTag;


/**
 * The default implementation of RFID tags tracker.
 *
 * <p>An instance of this class is used as a tracker of {@link RfCollector} if
 * another one not provided.</p>
 *
 * <p>This implementation caches all reported RFID tags. It reports that the
 * tag is appeared when its added to the cache for the first time, and reports
 * that the tag is disappeared when it is removed from this cache. This cache
 * also stores the last time the tag is read.</p>
 *
 * <p>The cache removal happens when the following conditions met:
 * <ul>
 * <li>a {@link RfDataMessage#getRfTransactionId() reading transaction} ended,
 * </li>
 * <li>the tag have not read during this transaction, and</li>
 * <li>a time is {@link #getInvalidationTimeout() out} since the last time the
 * tag is read.</li>
 * </ul>
 * </p>
 *
 * <p>A reading transaction considered ended when one of the following happens:
 * <ul>
 * <li>a transaction end {@link RfDataMessage#isRfTransactionEnd() explicitly
 * reported},</li>
 * <li>a {@link RfDataMessage#getRfTransactionId() transaction identifier}
 * changed, or</li>
 * <li>a time since the transaction start is {@link #getTransactionTimeout()
 * out}.</li>
 * </ul>
 * </p>
 */
public class DefaultRfTracker implements RfTracker {

	/**
	 * Default {@link #getTransactionTimeout() transaction timeout} value.
	 */
	public static final long DEFAULT_TRANSACTION_TIMEOUT = 5000L;

	/**
	 * Default {@link #getInvalidationTimeout() cache invalidation timeout}
	 * value.
	 */
	public static final long DEFAULT_INVALIDATION_TIMEOUT = 15000L;

	private long transactionTimeout = DEFAULT_TRANSACTION_TIMEOUT;
	private long invalidationTimeout = DEFAULT_INVALIDATION_TIMEOUT;
	boolean transactionStarted;
	private long transactionStart;
	private int transactionId;
	private RfTracking invalidator;
	private HashMap<RfTag, Long> remainingTags = new HashMap<>();
	private HashMap<RfTag, Long> presentTags = new HashMap<>();

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
	 * @param transactionTimeout new timeout value in milliseconds.
	 */
	public final void setTransactionTimeout(long transactionTimeout) {
		this.transactionTimeout =
				transactionTimeout <= 0
				? transactionTimeout : DEFAULT_TRANSACTION_TIMEOUT;
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
	 * <p>Set it to zero or negative value to invalidate the unread tags
	 * immediately after the transaction end.</p>
	 *
	 * @param invalidationTimeout new timeout value in milliseconds.
	 */
	public final void setInvalidationTimeout(long invalidationTimeout) {
		this.invalidationTimeout = invalidationTimeout;
	}

	@Override
	public void initRfTracker(RfTracking invalidator) {
		this.invalidator = invalidator;
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {

		final long timestamp = dataMessage.getTimestamp();
		final Long lastAppearance = new Long(timestamp);
		final RfTag tag = dataMessage.getRfTag();
		final boolean transactionEnd = dataMessage.isRfTransactionEnd();
		HashMap<RfTag, Long> pendingTags = null;
		boolean tagAppeared = false;

		synchronized (this) {

			final boolean transactionChanged =
					this.transactionStarted
					&& transactionChanged(timestamp, dataMessage);

			if (transactionChanged) {
				pendingTags = endTransaction();
			}
			if (tag != null) {
				tagAppeared = cacheTag(tag, lastAppearance);
			}
			if (transactionEnd) {
				// No need to start a new transaction tracking after explicit
				// transaction end.

				final HashMap<RfTag, Long> pending = endTransaction();

				if (pendingTags == null) {
					pendingTags = pending;
				} else {
					pendingTags.putAll(pending);
				}
			}
			if (!this.transactionStarted) {
				startTransaction(timestamp, dataMessage);
			}
		}
		if (tagAppeared) {
			this.invalidator.tagAppeared(tag);
		}
		if (pendingTags != null) {
			invalidatePendingTags(timestamp, pendingTags);
		}
	}

	private boolean transactionChanged(
			long timestamp,
			RfDataMessage tagMessage) {

		final int transactionId = tagMessage.getRfTransactionId();

		if (this.transactionId != transactionId) {
			return true;
		}

		return timestamp - this.transactionStart >= getTransactionTimeout();
	}

	private boolean cacheTag(RfTag tag, Long lastAppearance) {
		if (this.remainingTags.remove(tag) != null) {
			// Tag is among remaining. Not new.
			this.presentTags.put(tag, lastAppearance);
			return false;
		}
		// Tag is new?
		return this.presentTags.put(tag, lastAppearance) == null;
	}

	private HashMap<RfTag, Long> endTransaction() {

		final HashMap<RfTag, Long> pendingTags = this.remainingTags;

		this.remainingTags = this.presentTags;
		this.presentTags = new HashMap<>();
		this.transactionStarted = false;

		return pendingTags;
	}

	private void startTransaction(long timestamp, RfDataMessage message) {
		this.transactionStarted = true;
		this.transactionId = message.getRfTransactionId();
		this.transactionStart = timestamp;
	}

	private void invalidatePendingTags(
			long timestamp,
			HashMap<RfTag, Long> pendingTags) {

		final long timeout = getInvalidationTimeout();

		for (Map.Entry<RfTag, Long> e : pendingTags.entrySet()) {

			final RfTag tag = e.getKey();
			final Long lastAppearance = e.getValue();

			if (timestamp - lastAppearance.longValue() >= timeout) {
				this.invalidator.tagDisappeared(tag);
			} else {
				tagStillPresent(tag, lastAppearance);
			}
		}
	}

	private synchronized void tagStillPresent(
			RfTag tag,
			Long lastAppearance) {

		final Long removedPresent = this.presentTags.remove(tag);

		if (removedPresent != null) {
			this.presentTags.put(tag, removedPresent);
			return;
		}

		final Long replacedRemaining =
				this.remainingTags.put(tag, lastAppearance);

		if (replacedRemaining != null) {
			this.remainingTags.put(tag, replacedRemaining);
		}
	}

}
