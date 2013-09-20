package ru.aplix.ltk.core.util;


/**
 * Parameterized object interface.
 *
 * <p>Parameterized objects are able to store their data in {@link Parameters}.
 * </p>
 */
public interface Parameterized {

	/**
	 * Reads object data from parameters.
	 *
	 * @param params parameters to read object data from.
	 */
	void read(Parameters params);

	/**
	 * Writes object data to parameters.
	 *
	 * @param params parameters to write data to.
	 */
	void write(Parameters params);

}
