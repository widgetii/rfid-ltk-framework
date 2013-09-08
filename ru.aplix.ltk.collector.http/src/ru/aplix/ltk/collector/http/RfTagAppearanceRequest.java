package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import ru.aplix.ltk.core.collector.RfTagAppearance;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class RfTagAppearanceRequest
		implements RfTagAppearanceMessage, Parameterized {

	private RfTag rfTag;
	private RfTagAppearance appearance = RF_TAG_APPEARED;

	public RfTagAppearanceRequest(RfTagAppearanceMessage message) {
		this.rfTag = message.getRfTag();
		this.appearance = message.getAppearance();
	}

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
		params.set("rfTag", this.rfTag != null ? this.rfTag.getData() : null)
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
