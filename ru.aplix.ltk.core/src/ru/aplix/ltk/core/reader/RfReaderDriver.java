package ru.aplix.ltk.core.reader;


/**
 * RFID reader driver.
 *
 * <p>An implementation should be passed to {@link RfReader} constructor. All
 * operations on this driver are initiated by RFID reader.</p>
 *
 * <p>A reader context instance provided during {@link #initRfReader(
 * RfReaderContext) initialization} should be used to report events,
 * such as reader status updates, or RFID tags.</p>
 */
public interface RfReaderDriver {

	/**
	 * Returns RFID reader's port identifier.
	 *
	 * <p>This method can be called before the driver initialization.</p>
	 *
	 * @return identifier string.
	 */
	String getRefPortId();

	/**
	 * Initializes the driver.
	 *
	 * <p>Invoked by {@link RfReader} at most once prior to any other operation
	 * on this driver.<p>
	 *
	 * @param context RFID reader context.
	 */
	void initRfReader(RfReaderContext context);

	/**
	 * Starts the reader device.
	 *
	 * <p>Invoked by {@link RfReader} on first subscription.</p>
	 */
	void startRfReader();

	/**
	 * Stops the reader device.
	 *
	 * <p>Invoked by {@link RfReader} when last subscription revoked.</p>
	 */
	void stopRfReader();

}
