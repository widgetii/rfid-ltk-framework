package ru.aplix.ltk.core.source;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.message.MsgConsumer;


/**
 * RFID data source.
 *
 * <p>It is used by {@link RfCollector} as a source of RFID events.</p>
 *
 * <p>Any RFID reader can be used as {@link RfReader#toRfSource() RFID data
 * source}.</p>
 */
public interface RfSource {

	/**
	 * Requests an RFID source status updates.
	 *
	 * <p>After this method is called, the source may start to report the status
	 * updates.</p>
	 *
	 * <p>This method is called by {@link RfCollector} on the first
	 * {@link RfCollector#subscribe(MsgConsumer) subscription}.</p>
	 *
	 * @param context RFID data source context, which can be used to report
	 * RFID events.
	 */
	void requestRfStatus(RfStatusUpdater context);

	/**
	 * Requests RFID data input.
	 *
	 * <p>After this method is called, the source can start the reporting of
	 * {@link RfDataMessage RFID data}.</p>
	 *
	 * <p>This is called by collector on the first
	 * {@link RfCollectorHandle#requestTagAppearance(MsgConsumer) RFID tags
	 * appearance subscription}.</p>
	 *
	 * @param receiver RFID data receiver to send RFID data to.
	 */
	void requestRfData(RfDataReceiver receiver);

	/**
	 * Rejects RIFD data input.
	 *
	 * <p>After this method is called, the source should stop the reporting of
	 * {@link RfDataMessage RFID data}.</p>
	 *
	 * <p>This is called by collector when the last RFID tags appearance
	 * subscription revoked.</p>
	 */
	void rejectRfData();

	/**
	 * Rejects RFID status updates.
	 *
	 * <p>After this method is called, the source may start to report the status
	 * updates.</p>
	 *
	 * <p>This method is called by {@link RfCollector} when the last
	 * subscription revoked.</p>
	 */
	void rejectRfStatus();

}
