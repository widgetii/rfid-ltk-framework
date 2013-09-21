package ru.aplix.ltk.core.util;

import static java.lang.System.arraycopy;
import static java.util.Objects.requireNonNull;

import java.net.URL;
import java.util.UUID;


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
	 * URL parameter type.
	 */
	public static final SimpleParameterType<URL> URL_PARAMETER_TYPE =
			new URLParameterType();

	/**
	 * UUID parameter type.
	 */
	public static final SimpleParameterType<UUID> UUID_PARAMETER_TYPE =
			new UUIDParameterType();

	/**
	 * Retrieves parameter values.
	 *
	 * @param params parameters to retrieve the values from.
	 * @param name parameter name.
	 *
	 * @return retrieved values, or <code>null</code> if there is no such
	 * property.
	 */
	public abstract T[] getValues(Parameters params, String name);

	/**
	 * Stores parameter values as strings.
	 *
	 * @param params parameters to store values in.
	 * @param name parameter name.
	 * @param values parameter values to store.
	 */
	public abstract void setValues(Parameters params, String name, T[] values);

	/**
	 * Appends parameter values as strings.
	 *
	 * @param params parameters to store values in.
	 * @param name parameter name.
	 * @param values parameter values to append.
	 */
	public void addValues(Parameters params, String name, T[] values) {

		final String[] oldValues = params.store().getParam(name);

		setValues(params, name, values);
		if (oldValues == null) {
			return;
		}

		final String[] newValues = params.store().getParam(name);

		if (newValues == oldValues) {
			return;
		}

		final String[] result =
				new String[oldValues.length + newValues.length];

		arraycopy(oldValues, 0, result, 0, oldValues.length);
		arraycopy(newValues, 0, result, oldValues.length, newValues.length);

		params.store().setParam(name, result);
	}

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
