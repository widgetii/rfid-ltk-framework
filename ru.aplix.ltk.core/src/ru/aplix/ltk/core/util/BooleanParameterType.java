package ru.aplix.ltk.core.util;


final class BooleanParameterType extends SimpleParameterType<Boolean> {

	@Override
	public Boolean[] createValues(int length) {
		return new Boolean[length];
	}

	@Override
	public Boolean valueFromString(String string) {
		if (string.isEmpty()) {
			return null;
		}

		final String str = string.trim().toLowerCase();

		switch (str) {
		case "true":
		case "t":
		case "on":
		case "yes":
		case "y":
		case "1":
		case "+":
			return true;
		case "false":
		case "f":
		case "off":
		case "no":
		case "n":
		case "0":
		case "-":
			return false;
		}
		return null;
	}

	@Override
	public String valueToString(Boolean value) {
		return value.toString();
	}

}
