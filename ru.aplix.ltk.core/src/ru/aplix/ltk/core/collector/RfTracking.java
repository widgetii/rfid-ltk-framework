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
	 * Whether the next event is initial.
	 *
	 * <p>After this method returned <code>true</code>, all subsequent calls to
	 * it will return <code>false</code>, until tags tracking will be
	 * {@link RfTracker#stopRfTracking() stopped}, and
	 * {@link RfTracker#startRfTracking() started} again.</p>
	 *
	 * <p>This value can be used to set
	 * {@link RfTagAppearanceMessage#isInitialEvent() initial event flag}.</p>
	 *
	 * @return <code>true</code> if the next event is the first one after
	 * tracking start, or <code>false</code> otherwise.
	 */
	public final boolean nextEventIsInitial() {
		return this.collector.nextEventIsInitial();
	}

	@Override
	public final void updateStatus(RfStatusMessage status) {
		this.collector.updateStatus(status);
	}

	/**
	 * Reports the given RFID is appeared within reader's FOV.
	 *
	 * <p>This method consults {@link #nextEventIsInitial()} method to set the
	 * {@link RfTagAppearanceMessage#isInitialEvent() initial event flag}.</p>
	 *
	 * @param tag appeared RFID tag.
	 */
	public final void tagAppeared(RfTag tag) {
		updateTagAppearance(new RfTagAppearanceMessageImpl(
				tag,
				RF_TAG_APPEARED,
				nextEventIsInitial()));
	}

	/**
	 * Reports a change in RFID tag appearance.
	 *
	 * @param tagAppearance tag appearance change to report.
	 */
	public final void updateTagAppearance(
			RfTagAppearanceMessage tagAppearance) {
		if (tagAppearance.getAppearance().isPresent()) {
			this.collector.noError();
		}
		this.collector.tagAppearanceSubscriptions().sendMessage(tagAppearance);
	}

	/**
	 * Reports the given RFID tag disappeared from the reader's FOV.
	 *
	 * <p>This method consults {@link #nextEventIsInitial()} method to set the
	 * {@link RfTagAppearanceMessage#isInitialEvent() initial event flag}, but
	 * this is not generally required, as the first message after tracking start
	 * will be most probably a {@link #tagAppeared(RfTag) tag appearance} one.
	 * </p>
	 *
	 * @param tag disappeared RFID tag.
	 */
	public final void tagDisappeared(RfTag tag) {
		updateTagAppearance(new RfTagAppearanceMessageImpl(
				tag,
				RF_TAG_DISAPPEARED,
				nextEventIsInitial()));
	}

}
