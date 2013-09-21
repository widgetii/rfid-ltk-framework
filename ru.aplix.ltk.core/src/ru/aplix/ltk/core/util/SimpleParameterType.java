package ru.aplix.ltk.core.util;

import java.util.Arrays;


/**
 * Simple implementation of parameter value type.
 *
 * <p>Converts each parameter values individually. Ignores nulls. Converts empty
 * arrays to empty arrays. Converts arrays of nulls to nulls.</p>
 *
 * @param <T> parameters type.
 */
public abstract class SimpleParameterType<T> extends ParameterType<T> {

	@Override
	public T[] valuesFromStrings(String name, String[] strings) {

		final T[] values = createValues(strings.length);

		if (strings.length == 0) {
			return values;
		}

		int i = 0;

		for (String string : strings) {
			if (string == null) {
				continue;
			}

			final T value = valueFromString(string);

			if (value == null) {
				continue;
			}

			values[i++] = value;
		}

		if (i == 0) {
			return null;
		}
		if (i == values.length) {
			return values;
		}

		return Arrays.copyOf(values, i);
	}

	@Override
	public String[] valuesToStrings(String name, T[] values) {

		final String[] strings = new String[values.length];

		if (values.length == 0) {
			return strings;
		}

		int i = 0;

		for (T value : values) {
			if (value == null) {
				continue;
			}

			final String string = valueToString(value);

			if (string == null) {
				continue;
			}
			strings[i++] = string;
		}

		if (i == 0) {
			return null;
		}
		if (i == strings.length) {
			return strings;
		}

		return Arrays.copyOf(strings, i);
	}

	/**
	 * Creates an array of values.
	 *
	 * @param length the length of array to create.
	 *
	 * @return new array of he given length.
	 */
	public abstract T[] createValues(int length);

	/**
	 * Restores parameter value from string.
	 *
	 * @param string source string.
	 *
	 * @return restored value.
	 */
	public abstract T valueFromString(String string);

	/**
	 * Converts parameter value to string.
	 *
	 * @param value source value.
	 *
	 * @return string representation of the given value.
	 */
	public abstract String valueToString(T value);

}
