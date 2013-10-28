package ru.aplix.ltk.core.collector;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy.RF_INVALIDATION_TIMEOUT;
import static ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy.RF_TRANSACTION_TIMEOUT;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfError;
import ru.aplix.ltk.core.source.RfTag;


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
public class DefaultRfTracker implements RfTracker, Runnable {

	private final ScheduledExecutorService executorService =
			newSingleThreadScheduledExecutor();
	private final HashMap<RfTag, Long> tags = new HashMap<>();
	private ScheduledFuture<?> transactionChecker;
	private long transactionTimeout = RF_TRANSACTION_TIMEOUT.getDefault();
	private long invalidationTimeout = RF_INVALIDATION_TIMEOUT.getDefault();
	private long transactionStart;
	private long lastTimestamp;
	private volatile long localTimeDiff;
	private int transactionId;
	private RfTracking tracking;

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
	public final void setTransactionTimeout(long transactionTimeout) {
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
	public void startRfTracking() {
	}

	@Override
	public void initRfTracker(RfTracking tracking) {
		this.tracking = tracking;
	}

	@Override
	public void rfData(RfDataMessage dataMessage) {

		final long timestamp = dataMessage.getTimestamp();
		final RfTag tag = dataMessage.getRfTag();
		final long localTimeDiff = currentTimeMillis() - timestamp;
		final boolean transactionEnd = dataMessage.isRfTransactionEnd();
		HashSet<RfTag> obsoleteTags = null;
		boolean tagAppeared = false;

		synchronized (this) {
			if (timestamp > this.lastTimestamp) {
				this.lastTimestamp = timestamp;
				this.localTimeDiff = localTimeDiff;
			}

			final boolean transactionChanged =
					isTransactionStarted()
					&& transactionChanged(timestamp, dataMessage);

			if (transactionChanged) {
				obsoleteTags = endTransaction(timestamp);
			}
			if (tag != null) {
				if (obsoleteTags != null) {
					obsoleteTags.remove(tag);
				}
				tagAppeared = cacheTag(tag, timestamp);
			}
			if (transactionEnd) {

				final HashSet<RfTag> obsolete = endTransaction(timestamp);

				if (obsolete != null) {
					if (obsoleteTags == null) {
						obsoleteTags = obsolete;
					} else {
						obsoleteTags.addAll(obsolete);
					}
				}
			}
			if (!isTransactionStarted()) {
				startTransaction(timestamp, dataMessage);
			}
		}
		if (tagAppeared) {
			this.tracking.tagAppeared(tag);
		}
		if (obsoleteTags != null) {
			tagsDisappeared(obsoleteTags);
		}
	}

	@Override
	public synchronized void stopRfTracking() {
		stopTransaction();
	}

	@Override
	public void run() {
		try {

			final long timestamp = currentTimeMillis() - this.localTimeDiff;
			final HashSet<RfTag> obsoleteTags;

			synchronized (this) {
				obsoleteTags = removeObsoleteTags(timestamp);
			}
			if (obsoleteTags != null) {
				tagsDisappeared(obsoleteTags);
			}
		} catch (Throwable e) {
			this.tracking.updateStatus(new RfError(null, e));
		}
	}

	private final boolean isTransactionStarted() {
		return this.transactionChecker != null;
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

	private boolean cacheTag(RfTag tag, long timestamp) {

		final Long last = this.tags.put(tag, timestamp);

		if (last == null) {
			return true;// New tag.
		}
		if (last.longValue() > timestamp) {
			// Event in the past.
			this.tags.put(tag, last);
		}

		return false;
	}

	private HashSet<RfTag> endTransaction(long timestamp) {
		stopTransaction();
		return removeObsoleteTags(timestamp);
	}

	private void stopTransaction() {
		if (this.transactionChecker != null) {
			this.transactionChecker.cancel(false);
			this.transactionChecker = null;
		}
	}

	private void startTransaction(long timestamp, RfDataMessage message) {
		this.transactionId = message.getRfTransactionId();
		this.transactionStart = timestamp;
		this.transactionChecker = this.executorService.scheduleAtFixedRate(
				this,
				getTransactionTimeout(),
				getTransactionTimeout(),
				TimeUnit.MILLISECONDS);
	}

	private HashSet<RfTag> removeObsoleteTags(long timestamp) {

		final int size = this.tags.size();

		if (size == 0) {
			return null;
		}

		int index = 0;
		HashSet<RfTag> obsoleteTags = null;
		final long timeout = getInvalidationTimeout();
		final Iterator<Entry<RfTag, Long>> it =
				this.tags.entrySet().iterator();

		while (it.hasNext()) {

			final Map.Entry<RfTag, Long> e = it.next();
			final RfTag tag = e.getKey();
			final Long lastAppearance = e.getValue();

			if (timestamp - lastAppearance.longValue() < timeout) {
				++index;
				continue;
			}

			it.remove();
			if (obsoleteTags == null) {
				obsoleteTags = new HashSet<>(size - index);
			}
			obsoleteTags.add(tag);
		}

		return obsoleteTags;
	}

	private void tagsDisappeared(HashSet<RfTag> obsoleteTags) {
		for (RfTag tag : obsoleteTags) {
			this.tracking.tagDisappeared(tag);
		}
	}

}
