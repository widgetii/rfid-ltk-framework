package ru.aplix.ltk.core.util;


/**
 * Parameter.
 *
 * <p>A convenience way to declare parameters.</p>
 *
 * <p>Can be constructed with {@link ParameterType#parameter(String)} method.
 * </p>
 *
 * <p>An instances of this class are immutable.</p>
 *
 * @param <T> parameter type.
 */
public final class Parameter<T> {

	private final ParameterType<T> type;
	private final String name;
	private final T[] defaults;

	Parameter(ParameterType<T> type, String name) {
		this.type = type;
		this.name = name;
		this.defaults = null;
	}

	private Parameter(Parameter<T> prototype, T[] defaults) {
		this.type = prototype.getType();
		this.name = prototype.getName();
		this.defaults = defaults;
	}

	/**
	 * Parameter type.
	 *
	 * @return the parameter type this parameter is constructed by.
	 */
	public final ParameterType<T> getType() {
		return this.type;
	}

	/**
	 * Parameter name.
	 *
	 * @return parameter name passed to constructor method.
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Default parameter value.
	 *
	 * @return first default value, or <code>null</code> if not specified.
	 */
	public final T getDefault() {
		if (this.defaults == null || this.defaults.length == 0) {
			return null;
		}
		return this.defaults[0];
	}

	/**
	 * Default parameter values.
	 *
	 * @return default values assigned with {@link #byDefault(Object...)}
	 * method, or <code>null</code> if not assigned yet.
	 */
	public final T[] getDefaults() {
		return this.defaults;
	}

	/**
	 * Assigns default parameter values.
	 *
	 * @param defaults new default values.
	 *
	 * @return new instance of parameter with default values set.
	 */
	@SafeVarargs
	public final Parameter<T> byDefault(T... defaults) {
		if (defaults.length == 1 && defaults[0] == null) {
			return new Parameter<>(this, null);
		}
		return new Parameter<>(this, defaults);
	}

}
