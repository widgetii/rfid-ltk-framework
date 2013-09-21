package ru.aplix.ltk.core.util;


final class ObjectParameterType extends SimpleParameterType<Object> {

	@Override
	public Object[] createValues(int length) {
		return new Object[length];
	}

	@Override
	public Object valueFromString(String string) {
		return string;
	}

	@Override
	public String valueToString(Object value) {
		return value.toString();
	}

}
