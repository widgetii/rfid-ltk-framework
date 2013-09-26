package ru.aplix.ltk.core.util;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static ru.aplix.ltk.core.util.ArrayUtil.toArray;

import java.util.*;


/**
 * Various collection utilities.
 */
public final class CollectionUtil {

	/**
	 * Converts the given value to collection.
	 *
	 * <p>If the given value is a collection already, then returns it.</p>
	 *
	 * <p>If the given value is an array, then converts it to the list.</p>
	 *
	 * <p>If the given value is <code>null</code>, then returns an empty set.
	 * </p>
	 *
	 * <p>Otherwise returns a singleton set containing the given value.</p>
	 *
	 * @param value value to convert to collection.
	 *
	 * @return resulting collection.
	 */
	public static Collection<?> toCollection(Object value) {
		if (value == null) {
			return emptySet();
		}
		if (value instanceof Collection) {
			return (Collection<?>) value;
		}

		final Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			return Arrays.asList(toArray(value));
		}

		return Collections.singleton(value);
	}

	/**
	 * Converts the given value to collection.
	 *
	 * <p>If the given value is a set already, then returns it.</p>
	 *
	 * <p>If the given value is a collection or array, then returns a hash set
	 * of its elements.</p>
	 *
	 * <p>If the given value is a set already, then does nothing.</p>
	 *
	 * <p>If the given value is <code>null</code>, then returns an empty set.
	 * </p>
	 *
	 * <p>Otherwise returns a singleton set containing the given value.</p>
	 *
	 * @param value value to convert to collection.
	 *
	 * @return resulting collection.
	 */
	public static Set<?> toSet(Object value) {
		if (value == null) {
			return emptySet();
		}
		if (value instanceof Collection) {
			if (value instanceof Set) {
				return (Set<?>) value;
			}

			final Collection<?> collection = (Collection<?>) value;
			final HashSet<Object> set = new HashSet<>(collection.size());

			set.addAll(collection);

			return set;
		}

		final Class<?> clazz = value.getClass();

		if (!clazz.isArray()) {
			return singleton(value);
		}

		final Object[] array = ArrayUtil.toArray(value);
		final HashSet<Object> set = new HashSet<>(array.length);

		Collections.addAll(set, array);

		return set;
	}

	private CollectionUtil() {
	}

}
