package ru.aplix.ltk.core.util;

import java.util.Map;


/**
 * Parameters store interface.
 *
 * <p>An instance of this class is used by {@link Parameters} instance to
 * actually its parameters.<p>
 *
 * <p>If a store implementation is read-only, then parameters can not be
 * changed.</p>
 *
 * <p>If a store implementation does not implement an iteration, then parameters
 * can not be listed or serialized, e.g. URL-encoded.</p>
 */
public interface ParametersStore extends Iterable<Map.Entry<String, String[]>> {

	/**
	 * Returns parameter values.
	 *
	 * @param name target parameter name.
	 *
	 * @return parameter values, or <code>null</code> if no parameters with such
	 * name exists.
	 */
	String[] getParam(String name);

	/**
	 * Assigns parameter values.
	 *
	 * @param name target parameter name.
	 * @param values new parameter values, or <code>null</code> to remove it.
	 *
	 * @return previous parameter values, or <code>null</code> if parameter did
	 * not exist.
	 *
	 * @throws UnsupportedOperationException if parameter can not be changed,
	 * e.g. if this store is read-only.
	 */
	String[] setParam(String name, String[] values);

	/**
	 * Returns a store for nested parameters.
	 *
	 * <p>This method is called when constructing a
	 * {@link Parameters#sub(String) nested parameters}.</p>
	 *
	 * @param prefix the nested parameters prefix.
	 *
	 * @return nested parameters store, or <code>null</code> to use a default
	 * implementation.
	 */
	ParametersStore nestedStore(String prefix);

}
