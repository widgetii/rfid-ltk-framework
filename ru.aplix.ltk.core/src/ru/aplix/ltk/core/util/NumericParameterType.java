package ru.aplix.ltk.core.util;


/**
 * Numeric parameter type.
 *
 * <p>Represents numbers with {@code Object.toString()}. If a
 * {@code NumberFormatException} occurred during deserialization, then empty
 * (no-value) array of values returned.
 *
 * @param <N> numeric parameter type.
 */
public abstract class NumericParameterType<N extends Number>
		extends SimpleParameterType<N> {

	/**
	 * Integer parameter type.
	 */
	public static final NumericParameterType<Integer> INTEGER_PARAMETER_TYPE =
			new NumericParameterType<Integer>() {
				@Override
				public Integer[] createValues(int length) {
					return new Integer[length];
				}
				@Override
				public Integer valueFromString(String string) {
					return Integer.parseInt(string);
				}
			};

	/**
	 * Long parameter type.
	 */
	public static final NumericParameterType<Long> LONG_PARAMETER_TYPE =
			new NumericParameterType<Long>() {
				@Override
				public Long[] createValues(int length) {
					return new Long[length];
				}
				@Override
				public Long valueFromString(String string) {
					return Long.parseLong(string);
				}
			};

	@Override
	public N[] valuesFromStrings(String[] strings) {
		try {
			return super.valuesFromStrings(strings);
		} catch (NumberFormatException e) {
			return createValues(0);
		}
	}

	@Override
	public String valueToString(N value) {
		return value.toString();
	}

}
