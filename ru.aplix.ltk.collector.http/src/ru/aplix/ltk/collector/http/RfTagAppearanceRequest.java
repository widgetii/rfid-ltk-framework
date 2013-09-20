package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import ru.aplix.ltk.core.collector.RfTagAppearance;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


/**
 * An HTTP request containing the info about a change in RFID tag appearance.
 */
public class RfTagAppearanceRequest
		implements RfTagAppearanceMessage, Parameterized {

	private ClrClientId clientId;
	private RfTag rfTag;
	private RfTagAppearance appearance = RF_TAG_APPEARED;

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
	public RfTag getRfTag() {
		return this.rfTag;
	}

	@Override
	public RfTagAppearance getAppearance() {
		return this.appearance;
	}

	@Override
	public void read(Parameters params) {
		this.rfTag = tagFromParameters(params);
		this.appearance = params.enumValueOf(
				RfTagAppearance.class,
				"appearance",
				RF_TAG_APPEARED,
				getAppearance());
	}

	@Override
	public void write(Parameters params) {
		params.set("client", this.clientId.getUUID().toString())
		.set("type", "tag")
		.set("rfTag", this.rfTag != null ? this.rfTag.getData() : null)
		.set("appearance", getAppearance());
	}

	private RfTag tagFromParameters(Parameters params) {

		final RfTag oldTag = getRfTag();
		final byte[] data = params.binaryValueOf(
				"rfTag",
				null,
				oldTag != null ? oldTag.getData() : null);

		return data != null ? new RfTag(data) : null;
	}

}
