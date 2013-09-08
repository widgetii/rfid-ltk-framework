package ru.aplix.ltk.core.collector;

import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.source.RfStatusUpdater;
import ru.aplix.ltk.core.source.RfTag;


/**
 * An RFID tag tracking.
 *
 * <p>An instance of this class is passed to the {@link RfTracker#initRfTracker(
 * RfTracking) RFID tags tracker}, so the latter can report the changes in
 * tag appearance.</p>
 */
public final class RfTracking implements RfStatusUpdater {

	private final RfCollector collector;

	RfTracking(RfCollector collector) {
		this.collector = collector;
	}

	/**
	 * Reports unexpected reader status updates.
	 *
	 * <p>May be used to report errors occurred in tracker to all subscribers.
	 * </p>
	 *
	 * @param status status to report.
	 */
	@Override
	public final void updateStatus(RfStatusMessage status) {
		this.collector.collectorSubscriptions().sendMessage(status);
	}

	/**
	 * Reports the given RFID is appeared within reader's FOV.
	 *
	 * @param tag appeared RFID tag.
	 */
	public final void tagAppeared(RfTag tag) {
		this.collector.tagAppearanceSubscriptions().sendMessage(
				new RfTagAppearanceMessageImpl(tag, RF_TAG_APPEARED));
	}

	/**
	 * Reports the given RFID tag disappeared from the reader's FOV.
	 *
	 * @param tag disappeared RFID tag.
	 */
	public final void tagDisappeared(RfTag tag) {
		this.collector.tagAppearanceSubscriptions().sendMessage(
				new RfTagAppearanceMessageImpl(tag, RF_TAG_DISAPPEARED));
	}

}
