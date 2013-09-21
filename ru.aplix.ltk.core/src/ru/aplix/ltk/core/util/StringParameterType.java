package ru.aplix.ltk.core.util;


final class StringParameterType extends SimpleParameterType<String> {

	@Override
	public String[] valuesFromStrings(String[] strings) {
		if (!hasNulls(strings)) {
			return strings;
		}
		return super.valuesFromStrings(strings);
	}

	@Override
	public String[] valuesToStrings(String[] values) {
		if (!hasNulls(values)) {
			return values;
		}
		return super.valuesToStrings(values);
	}

	@Override
	public String[] createValues(int length) {
		return new String[length];
	}

	@Override
	public String valueFromString(String string) {
		return string;
	}

	@Override
	public String valueToString(String value) {
		return value;
	}

	private static boolean hasNulls(String[] strings) {
		for (String string : strings) {
			if (string == null) {
				return true;
			}
		}
		return false;
	}

}
