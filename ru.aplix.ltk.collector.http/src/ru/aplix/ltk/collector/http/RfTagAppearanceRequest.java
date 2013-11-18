package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.collector.http.RfStatusRequest.CLIENT;
import static ru.aplix.ltk.collector.http.RfStatusRequest.TYPE;
import static ru.aplix.ltk.collector.http.RfTagParameterType.RF_TAG_PARAMETER_TYPE;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.util.IntSet.EMPTY_INT_SET;
import static ru.aplix.ltk.core.util.IntSet.INT_SET_PARAMATER_TYPE;
import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.BOOLEAN_PARAMETER_TYPE;
import ru.aplix.ltk.core.collector.RfTagAppearance;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.util.*;


/**
 * An HTTP request containing the info about a change in RFID tag appearance.
 */
public class RfTagAppearanceRequest
		implements RfTagAppearanceMessage, Parameterized {

	private static final Parameter<Long> EVENT_ID =
			LONG_PARAMETER_TYPE.parameter("eventId").byDefault(0L);
	private static final Parameter<Boolean> INITIAL_EVENT =
			BOOLEAN_PARAMETER_TYPE.parameter("initialEvent").byDefault(false);
	private static final Parameter<Long> TIMESTAMP =
			LONG_PARAMETER_TYPE.parameter("timestamp").byDefault(0L);
	private static final Parameter<IntSet> ANTENNAS =
			INT_SET_PARAMATER_TYPE.parameter("antennas")
			.byDefault(EMPTY_INT_SET);
	private static final Parameter<RfTag> RF_TAG =
			RF_TAG_PARAMETER_TYPE.parameter("rfTag");
	private static final Parameter<RfTagAppearance> APPEARANCE =
			new EnumParameterType<>(RfTagAppearance.class)
			.parameter("appearance")
			.byDefault(RF_TAG_APPEARED);

	private ClrClientId clientId;
	private long eventId;
	private long timestamp;
	private IntSet antennas = ANTENNAS.getDefault();
	private RfTag rfTag;
	private RfTagAppearance appearance = APPEARANCE.getDefault();
	private boolean initialEvent;

	/**
	 * Constructs an empty request.
	 */
	public RfTagAppearanceRequest() {
	}

	/**
	 * Constructs a request by the given message.
	 *
	 * @param clientId a client identifier to send the request to.
	 * @param message a change in RFID tag appearance to send.
	 */
	public RfTagAppearanceRequest(
			ClrClientId clientId,
			RfTagAppearanceMessage message) {
		this.clientId = clientId;
		this.eventId = message.getEventId();
		this.initialEvent = message.isInitialEvent();
		this.timestamp = message.getTimestamp();
		this.rfTag = message.getRfTag();
		this.appearance = message.getAppearance();
	}

	/**
	 * Reconstruct a request from the given parameters.
	 *
	 * @param parameters parameters containing the constructed request's data.
	 */
	public RfTagAppearanceRequest(Parameters parameters) {
		read(parameters);
	}

	@Override
	public long getEventId() {
		return this.eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	@Override
	public boolean isInitialEvent() {
		return this.initialEvent;
	}

	public void setInitialEvent(boolean initialEvent) {
		this.initialEvent = initialEvent;
	}

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public IntSet getAntennas() {
		return this.antennas;
	}

	public void setAntennas(IntSet antennas) {
		this.antennas = antennas != null ? antennas : EMPTY_INT_SET;
	}

	@Override
	public RfTag getRfTag() {
		return this.rfTag;
	}

	public void setRfTag(RfTag rfTag) {
		this.rfTag = rfTag;
	}

	@Override
	public RfTagAppearance getAppearance() {
		return this.appearance;
	}

	public void setAppearance(RfTagAppearance appearance) {
		this.appearance = appearance;
	}

	@Override
	public void read(Parameters params) {
		this.eventId = params.valueOf(EVENT_ID, getEventId());
		this.timestamp = params.valueOf(TIMESTAMP, getTimestamp());
		this.antennas = params.valueOf(ANTENNAS, getAntennas());
		this.rfTag = params.valueOf(RF_TAG, getRfTag());
		this.appearance = params.valueOf(APPEARANCE, getAppearance());
		this.initialEvent = params.valueOf(INITIAL_EVENT, isInitialEvent());
	}

	@Override
	public void write(Parameters params) {
		if (this.clientId != null) {
			params.set(CLIENT, this.clientId.getUUID());
		}
		params.set(TYPE, "tag")
		.set(EVENT_ID, getEventId())
		.set(TIMESTAMP, getTimestamp())
		.set(ANTENNAS, getAntennas())
		.set(RF_TAG, getRfTag())
		.set(APPEARANCE, getAppearance());
		if (isInitialEvent()) {
			params.set(INITIAL_EVENT, true);
		}
	}

}
