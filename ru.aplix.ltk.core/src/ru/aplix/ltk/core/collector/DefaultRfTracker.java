package ru.aplix.ltk.core.collector;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy.RF_INVALIDATION_TIMEOUT;
import static ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy.RF_TRANSACTION_TIMEOUT;
import static ru.aplix.ltk.core.util.IntSet.EMPTY_INT_SET;
import static ru.aplix.ltk.core.util.IntSet.singletonIntSet;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfError;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.util.IntSet;


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
	private final HashMap<RfTag, TagPresence> tags = new HashMap<>();
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
		HashMap<RfTag, TagPresence> obsoleteTags = null;
		TagPresence tagAppeared = null;

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
				tagAppeared = cacheTag(
						tag,
						timestamp,
						dataMessage.getAntennaId());
			}
			if (transactionEnd) {

				final HashMap<RfTag, TagPresence> obsolete =
						endTransaction(timestamp);

				if (obsolete != null) {
					if (obsoleteTags == null) {
						obsoleteTags = obsolete;
					} else {
						obsoleteTags.putAll(obsolete);
					}
				}
			}
			if (!isTransactionStarted()) {
				startTransaction(timestamp, dataMessage);
			}
		}
		if (tagAppeared != null) {
			this.tracking.tagAppeared(tagAppeared.getAntennas(), tag);
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
			final HashMap<RfTag, TagPresence> obsoleteTags;

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

	private TagPresence cacheTag(RfTag tag, long timestamp, int antennaId) {

		final TagPresence presence =
				new TagPresence(timestamp, singletonIntSet(antennaId));
		final TagPresence last = this.tags.put(tag, presence);

		if (last == null) {
			return presence;// New tag.
		}
		if (last.getLastAppearance() > timestamp) {
			// Event in the past.
			last.addAntennas(presence.getAntennas());
			this.tags.put(tag, last);
		} else {
			presence.addAntennas(last.getAntennas());
		}

		return null;
	}

	private HashMap<RfTag, TagPresence> endTransaction(long timestamp) {
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

	private HashMap<RfTag, TagPresence> removeObsoleteTags(long timestamp) {

		final int size = this.tags.size();

		if (size == 0) {
			return null;
		}

		int index = 0;
		HashMap<RfTag, TagPresence> obsoleteTags = null;
		final long timeout = getInvalidationTimeout();
		final Iterator<Entry<RfTag, TagPresence>> it =
				this.tags.entrySet().iterator();

		while (it.hasNext()) {

			final Map.Entry<RfTag, TagPresence> e = it.next();
			final RfTag tag = e.getKey();
			final TagPresence presence = e.getValue();

			if (timestamp - presence.getLastAppearance() < timeout) {
				++index;
				continue;
			}

			it.remove();
			if (obsoleteTags == null) {
				obsoleteTags = new HashMap<>(size - index);
			}
			obsoleteTags.put(tag, presence);
		}

		return obsoleteTags;
	}

	private void tagsDisappeared(HashMap<RfTag, TagPresence> obsoleteTags) {
		for (Map.Entry<RfTag, TagPresence> e : obsoleteTags.entrySet()) {
			this.tracking.tagDisappeared(
					e.getValue().getAntennas(),
					e.getKey());
		}
	}

	private static final class TagPresence {

		private final long lastAppearance;
		private IntSet antennas;

		TagPresence(long lastAppearance, IntSet antennas) {
			this.lastAppearance = lastAppearance;
			this.antennas = antennas != null ? antennas : EMPTY_INT_SET;
		}

		public final long getLastAppearance() {
			return this.lastAppearance;
		}

		public final IntSet getAntennas() {
			return this.antennas;
		}

		public final void addAntennas(IntSet antennas) {
			this.antennas = this.antennas.union(antennas);
		}

	}

}
