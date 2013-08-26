package ru.aplix.ltk.core.collector;

import java.util.HashMap;
import java.util.Map;

import ru.aplix.ltk.core.reader.RfReadMessage;
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
 * <li>a {@link RfReadMessage#getRfTransactionId() reading transaction} ended,
 * </li>
 * <li>the tag have not read during this transaction, and</li>
 * <li>a time is {@link #getInvalidationTimeout() out} since the last time the
 * tag is read.</li>
 * </ul>
 * </p>
 *
 * <p>A reading transaction considered ended when one of the following happens:
 * <ul>
 * <li>a transaction end {@link RfReadMessage#isRfTransactionEnd() explicitly
 * reported},</li>
 * <li>a {@link RfReadMessage#getRfTransactionId() transaction identifier}
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
	public void rfRead(RfReadMessage readMessage) {

		final long now = System.currentTimeMillis();
		final Long lastAppearance = new Long(now);
		final RfTag tag = readMessage.getRfTag();
		final boolean transactionEnd = readMessage.isRfTransactionEnd();
		final boolean transactionChanged =
				!transactionEnd && transactionChanged(now, readMessage);
		HashMap<RfTag, Long> pendingTags = null;
		boolean tagAppeared = false;

		synchronized (this) {
			if (transactionChanged) {
				pendingTags = updateTransaction(now, readMessage);
			}
			if (tag != null) {
				tagAppeared = cacheTag(tag, lastAppearance);
			}
			if (transactionEnd) {
				pendingTags = updateTransaction(now, readMessage);
			}
		}
		if (tagAppeared) {
			this.invalidator.tagAppeared(tag);
		}
		if (pendingTags != null) {
			invalidatePendingTags(now, pendingTags);
		}
	}

	private boolean transactionChanged(
			long now,
			RfReadMessage tagMessage) {

		final int transactionId = tagMessage.getRfTransactionId();

		if (this.transactionId != transactionId) {
			return true;
		}

		return now - this.transactionStart >= getTransactionTimeout();
	}

	private boolean cacheTag(RfTag tag, Long lastAppearance) {
		if (this.remainingTags.remove(tag) != null) {
			// Tag is among remaining. Not new.
			this.presentTags.put(tag, lastAppearance);
			return false;
		}
		// Tag is new?
		return this.presentTags.put(tag, lastAppearance) != null;
	}

	private HashMap<RfTag, Long> updateTransaction(
			long now,
			RfReadMessage message) {

		final HashMap<RfTag, Long> pendingTags = this.remainingTags;

		this.remainingTags = this.presentTags;
		this.presentTags = new HashMap<>();
		this.transactionId = message.getRfTransactionId();
		this.transactionStart = now;

		return pendingTags;
	}

	private void invalidatePendingTags(
			long now,
			HashMap<RfTag, Long> pendingTags) {

		final long timeout = getInvalidationTimeout();

		for (Map.Entry<RfTag, Long> e : pendingTags.entrySet()) {

			final RfTag tag = e.getKey();
			final Long lastAppearance = e.getValue();

			if (now - lastAppearance.longValue() >= timeout) {
				this.invalidator.tagDisappeared(tag);
			} else {
				tagStillPresent(tag, lastAppearance);
			}
		}
	}

	private synchronized void  tagStillPresent(
			RfTag tag,
			Long lastAppearance) {

		final Long removedRemaining = this.remainingTags.remove(tag);

		if (removedRemaining != null) {
			this.remainingTags.put(tag, removedRemaining);
			return;
		}

		final Long removedPresent =
				this.presentTags.put(tag, lastAppearance);

		if (removedPresent != null) {
			this.presentTags.put(tag, removedPresent);
		}
	}

}
