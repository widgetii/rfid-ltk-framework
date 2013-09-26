package ru.aplix.ltk.core.util;

import java.lang.reflect.Array;


/**
 * Array manipulation utilities.
 */
public final class ArrayUtil {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * Converts any object to array.
	 *
	 * <p>Converts an array of primitive type to array of wrapped values.</p>
	 *
	 * <p>Converts <code>null</code> to empty array.</p>
	 *
	 * <p>Converts a non-array value to array with single value.</p>
	 *
	 * @param object to convert to array.
	 *
	 * @return result array.
	 */
	public static Object[] toArray(Object object) {
		if (object == null) {
			return EMPTY_ARRAY;
		}

		final Class<?> clazz = object.getClass();

		if (!clazz.isArray()) {
			return new Object[] {object};
		}

		final Class<?> type = clazz.getComponentType();

		if (!type.isPrimitive()) {
			return (Object[]) object;
		}

		final int length = Array.getLength(object);
		final Object[] array = new Object[length];

		for (int i = 0; i < length; ++i) {
			array[i] = Array.get(object, i);
		}

		return array;
	}

	private ArrayUtil() {
	}

}
