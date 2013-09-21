package ru.aplix.ltk.core.util;

import static java.lang.System.arraycopy;

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
	public T[] getValues(Parameters params, String name) {

		final String[] strings = params.store().getParam(name);

		if (strings == null) {
			return null;
		}

		final T[] values = valuesFromStrings(strings);

		if (values == null) {
			return null;
		}

		return values;
	}

	@Override
	public void setValues(Parameters params, String name, T[] values) {
		params.store().setParam(
				name,
				values == null ? null : valuesToStrings(values));
	}

	@Override
	public void addValues(Parameters params, String name, T[] values) {

		final String[] strings = valuesToStrings(values);

		if (strings == null) {
			return;
		}

		final String[] existing = params.store().setParam(name, strings);

		if (existing == null) {
			return;
		}
		if (strings.length == 0) {
			params.store().setParam(name, existing);
			return;
		}

		final String[] newStrings =
				new String[existing.length + strings.length];

		arraycopy(existing, 0, newStrings, 0, existing.length);
		arraycopy(strings, 0, newStrings, existing.length, strings.length);

		params.store().setParam(name, newStrings);
	}

	public T[] valuesFromStrings(String[] strings) {

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

	public String[] valuesToStrings(T[] values) {

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
