package ru.aplix.ltk.core.util;

import static java.util.Objects.requireNonNull;


/**
 * Parameter type.
 *
 * <p>Used by {@link Parameters} to serialize and deserialize parameter
 * values of this type.</p>
 *
 * @param <T> parameter type.
 */
public abstract class ParameterType<T> {

	/**
	 * String parameter type.
	 */
	public static final SimpleParameterType<String> STRING_PARAMETER_TYPE =
			new StringParameterType();

	/**
	 * Generic object parameter type.
	 *
	 * <p>Converts objects to strings with {@code Object.toString()} method.
	 * Uses original strings as restored parameter values.</p>
	 */
	public static final SimpleParameterType<Object> OBJECT_PARAMETER_TYPE =
			new ObjectParameterType();

	/**
	 * Binary data parameter type.
	 *
	 * <p>Represents byte arrays as hexadecimal strings.</p>
	 */
	public static final SimpleParameterType<byte[]> BINARY_PARAMETER_TYPE =
			new BinaryParameterType();

	/**
	 * Restores parameter values from strings.
	 *
	 * @param name parameter name.
	 * @param strings strings to restore values from.
	 *
	 * @return restored values.
	 */
	public abstract T[] valuesFromStrings(String name, String[] strings);

	/**
	 * Converts parameter values to strings.
	 *
	 * @param name parameter name.
	 * @param values parameter values.
	 *
	 * @return string representations of the given values.
	 */
	public abstract String[] valuesToStrings(String name, T[] values);

	/**
	 * Constructs parameter of this type.
	 *
	 * @param name constructed parameter name.
	 *
	 * @return new parameter with the given name.
	 */
	public final Parameter<T> parameter(String name) {
		requireNonNull(name, "Parameter name not specified");
		return new Parameter<>(this, name);
	}

}
