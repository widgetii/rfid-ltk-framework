package ru.aplix.ltk.core.util;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;


/**
 * Enumeration parameter type.
 *
 * <p>Represents enumeration constants with their names.</p>
 *
 * @param <E> enumeration parameter type.
 */
public class EnumParameterType<E extends Enum<E>>
		extends SimpleParameterType<E> {

	private final Class<E> enumType;

	/**
	 * Constructs enumeration parameter type.
	 *
	 * @param enumType enumeration class.
	 */
	public EnumParameterType(Class<E> enumType) {
		requireNonNull(enumType, "Enumeration type not specified");
		this.enumType = enumType;
	}

	/**
	 * Enumeration class.
	 *
	 * @return enumeration class passed to the constructor.
	 */
	public final Class<E> getEnumType() {
		return this.enumType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E[] createValues(int length) {
		return (E[]) Array.newInstance(getEnumType(), length);
	}

	@Override
	public E valueFromString(String string) {
		return Enum.valueOf(getEnumType(), string);
	}

	@Override
	public String valueToString(E value) {
		return value.name();
	}

}
