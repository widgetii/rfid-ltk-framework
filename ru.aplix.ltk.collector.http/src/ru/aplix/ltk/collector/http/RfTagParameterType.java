package ru.aplix.ltk.collector.http;

import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.core.util.ParameterType;
import ru.aplix.ltk.core.util.Parameters;


final class RfTagParameterType extends ParameterType<RfTag> {

	static final ParameterType<RfTag> RF_TAG_PARAMETER_TYPE =
			new RfTagParameterType();

	private RfTagParameterType() {
	}

	@Override
	public RfTag[] getValues(Parameters params, String name) {

		final byte[][] values = params.valuesOf(BINARY_PARAMETER_TYPE, name);

		if (values == null) {
			return null;
		}
		if (values.length == 0) {
			return new RfTag[0];
		}

		return new RfTag[] {new RfTag(values[0])};
	}

	@Override
	public void setValues(Parameters params, String name, RfTag[] values) {
		if (values.length == 0) {
			params.set(BINARY_PARAMETER_TYPE, name);
		} else {
			params.set(BINARY_PARAMETER_TYPE, name, values[0].getData());
		}
	}

}
