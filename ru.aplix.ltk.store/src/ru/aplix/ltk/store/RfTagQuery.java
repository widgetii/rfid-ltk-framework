package ru.aplix.ltk.store;

import java.util.List;


/**
 * RFID tags query.
 *
 * <p>Can be constructed by {@link RfStore#tagQuery()}, or
 * {@link RfReceiver#tagQuery()}.</p>
 *
 * <p>The query is executed on {@link #find()} method call. Query fields should
 * be filled before that. Only fields that are set will be used for the request.
 * </p>
 */
public abstract class RfTagQuery {

	private final RfReceiver<?> receiver;
	private String tag;
	private long since;
	private int offset;
	private int limit;

	/**
	 * Constructs a query.
	 *
	 * @param receiver RFID receiver to search for tags events in, or
	 * <code>null</code> to search among all stored tag events.
	 */
	public RfTagQuery(RfReceiver<?> receiver) {
		this.receiver = receiver;
	}

	/**
	 * Target RFID receiver.
	 *
	 * @return RFID receiver passed to the constructor, or <code>null</code>
	 * if this query searches among all stored tag events.
	 */
	public final RfReceiver<?> getReceiver() {
		return this.receiver;
	}

	/**
	 * RFID tag identifier to search for.
	 *
	 * @return hexadecimal tag presentation string, or <code>null</code> if not
	 * set.
	 */
	public final String getTag() {
		return this.tag;
	}

	/**
	 * Sets RFID tag identifier to search for.
	 *
	 * @param tag hexadecimal tag presentation string, or <code>null</code> to
	 * search for all tag events.
	 *
	 * @return this query.
	 */
	public final RfTagQuery setTag(String tag) {
		this.tag = tag;
		return this;
	}

	/**
	 * Time to search for events starting from.
	 *
	 * @return UNIX time in milliseconds.
	 */
	public final long getSince() {
		return this.since;
	}

	/**
	 * Sets time to search for events starting from.
	 *
	 * @param since UNIX time in milliseconds.
	 *
	 * @return this query.
	 */
	public final RfTagQuery setSince(long since) {
		this.since = since;
		return this;
	}

	/**
	 * An index of the first event to load.
	 *
	 * @return non-negative value.
	 */
	public final int getOffset() {
		return this.offset;
	}

	/**
	 * Sets an index of the first event to load.
	 *
	 * @param offset non-negative value. A negative value equals to zero.
	 *
	 * @return this query.
	 */
	public final RfTagQuery setOffset(int offset) {
		this.offset = offset < 0 ? 0 : offset;
		return this;
	}

	/**
	 * The maximum number of events to load
	 *
	 * @return a positive limit, or zero to not limit the results.
	 */
	public final int getLimit() {
		return this.limit;
	}

	/**
	 * Sets the maximum number of events to load.
	 *
	 * @param limit non-negative number. A negative value equals to zero.
	 *
	 * @return this query.
	 */
	public final RfTagQuery setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * Searches for tag events.
	 *
	 * <p>This method also updates a {@link #getTotalCount() total count} of
	 * events.</p>
	 *
	 * @return a list of found tag events.
	 */
	public abstract List<? extends RfTagEvent> find();

	/**
	 * A total count of tag events matching this query.
	 *
	 * <p>This value is updated only after the {@link #find() query execution}.
	 * </p>
	 *
	 * @return total count of events, or zero if query is not executed yet.
	 */
	public abstract long getTotalCount();

}
