package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.collector.http.RfStatusRequest.CLIENT;
import static ru.aplix.ltk.collector.http.RfStatusRequest.TYPE;
import static ru.aplix.ltk.collector.http.RfTagParameterType.RF_TAG_PARAMETER_TYPE;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
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
	private static final Parameter<Long> TIMESTAMP =
			LONG_PARAMETER_TYPE.parameter("timestamp").byDefault(0L);
	private static final Parameter<RfTag> RF_TAG =
			RF_TAG_PARAMETER_TYPE.parameter("rfTag");
	private static final Parameter<RfTagAppearance> APPEARANCE =
			new EnumParameterType<>(RfTagAppearance.class)
			.parameter("appearance")
			.byDefault(RF_TAG_APPEARED);

	private ClrClientId clientId;
	private long eventId;
	private long timestamp;
	private RfTag rfTag;
	private RfTagAppearance appearance = APPEARANCE.getDefault();

	/**
	 * Constructs a request.
	 *
	 * @param clientId a client identifier to send the request to.
	 * @param message a change in RFID tag appearance to send.
	 */
	public RfTagAppearanceRequest(
			ClrClientId clientId,
			RfTagAppearanceMessage message) {
		this.clientId = clientId;
		this.eventId = message.getEventId();
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

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public RfTag getRfTag() {
		return this.rfTag;
	}

	@Override
	public RfTagAppearance getAppearance() {
		return this.appearance;
	}

	@Override
	public void read(Parameters params) {
		this.eventId = params.valueOf(EVENT_ID, getEventId());
		this.timestamp = params.valueOf(TIMESTAMP, getTimestamp());
		this.rfTag = params.valueOf(RF_TAG, getRfTag());
		this.appearance = params.valueOf(APPEARANCE, getAppearance());
	}

	@Override
	public void write(Parameters params) {
		params.set(CLIENT, this.clientId.getUUID())
		.set(TYPE, "tag")
		.set(EVENT_ID, getEventId())
		.set(TIMESTAMP, getTimestamp())
		.set(RF_TAG, getRfTag())
		.set(APPEARANCE, getAppearance());
	}

}
