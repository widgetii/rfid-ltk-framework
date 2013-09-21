package ru.aplix.ltk.core.util;

import static java.util.Objects.requireNonNull;
import static ru.aplix.ltk.core.util.ParameterType.STRING_PARAMETER_TYPE;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Arbitrary parameters represented as strings.
 *
 * <p>Each parameters has a name, and may have zero or more values.</p>
 *
 * <p>This can be used to transfer data over HTTP or write it to the file.</p>
 *
 * <p>The actual store of parameters is implemented by {@link ParametersStore}
 * instance.</p>
 *
 * <p>The {@link Parameterized parameterized objects} can store their data in
 * parameters, and thus can transfer, read, and store it where {@link #store()
 * possible}.</p>
 *
 * <p>This class contains a lot of handy methods for accessing and assigning
 * parameter values and converting them to/from different data formats.</p>
 */
public final class Parameters implements Parameterized {

	/**
	 * UTF-8 - the default URL encoding charset.
	 */
	public static final String UTF_8 = "UTF-8";

	private final ParametersStore store;

	/**
	 * Constructs empty parameters stored in {@code HashMap}.
	 */
	public Parameters() {
		this(new HashMap<String, String[]>());
	}

	/**
	 * Constructs empty parameters stored in {@code HashMap} with the given
	 * initial capacity.
	 *
	 * @param capacity initial {@code HashMap} capacity.
	 */
	public Parameters(int capacity) {
		this(new HashMap<String, String[]>(capacity));
	}

	/**
	 * Construct parameters store in the given map.
	 *
	 * @param map map to store parameters in.
	 */
	public Parameters(Map<String, String[]> map) {
		this.store = new ParametersMap(map);
	}

	/**
	 * Construct parameters stored in the given store.
	 *
	 * @param store parameters store to use.
	 */
	public Parameters(ParametersStore store) {
		requireNonNull(store, "Parameters store not specified");
		this.store = store;
	}

	/**
	 * Parameters store.
	 *
	 * @return the store passed to the constructor or constructed
	 * automatically.
	 */
	public final ParametersStore store() {
		return this.store;
	}

	public final boolean have(String name) {
		return this.store.getParam(name) != null;
	}

	public final <T> T[] valuesOf(ParameterType<T> type, String name) {
		return valuesOf(type, name, null, null);
	}

	public final <T> T[] valuesOf(
			ParameterType<T> type,
			String name,
			T[] defaultValues) {
		return valuesOf(type, name, defaultValues, defaultValues);
	}

	public final <T> T[] valuesOf(
			ParameterType<T> type,
			String name,
			T[] noValues,
			T[] defaultValues) {

		final T[] values = type.getValues(this, name);

		if (values == null) {
			return defaultValues;
		}
		if (values.length == 0) {
			return noValues;
		}

		return values;
	}

	public final <T> T[] valuesOf(Parameter<T> parameter) {
		return valuesOf(
				parameter.getType(),
				parameter.getName(),
				parameter.getDefaults(),
				parameter.getDefaults());
	}

	public final <T> T[] valuesOf(Parameter<T> parameter, T[] defaultValues) {
		return valuesOf(
				parameter.getType(),
				parameter.getName(),
				parameter.getDefaults(),
				defaultValues);
	}

	public final String[] valuesOf(String name) {
		return valuesOf(name, null, null);
	}

	public final String[] valuesOf(String name, String[] defaultValues) {
		return valuesOf(name, defaultValues, defaultValues);
	}

	public final String[] valuesOf(
			String name,
			String[] noValues,
			String[] defaultValues) {

		final String[] values = store().getParam(name);

		if (values == null) {
			return defaultValues;
		}
		if (values.length == 0) {
			return noValues;
		}

		return values;
	}

	public final <T> T valueOf(ParameterType<T> type, String name) {
		return valueOf(type, name, null, null);
	}

	public final <T> T valueOf(
			ParameterType<T> type,
			String name,
			T defaultValue) {
		return valueOf(type, name, defaultValue, defaultValue);
	}

	public final <T> T valueOf(
			ParameterType<T> type,
			String name,
			T noValue,
			T defaultValue) {

		final T[] values = type.getValues(this, name);

		if (values == null) {
			return defaultValue;
		}
		if (values.length == 0) {
			return noValue;
		}

		return values[0];
	}

	public final <T> T valueOf(Parameter<T> parameter) {
		return valueOf(
				parameter.getType(),
				parameter.getName(),
				parameter.getDefault(),
				parameter.getDefault());
	}

	public final <T> T valueOf(Parameter<T> parameter, T defaultValue) {
		return valueOf(
				parameter.getType(),
				parameter.getName(),
				parameter.getDefault(),
				defaultValue);
	}

	public final String valueOf(String name) {
		return valueOf(name, null);
	}

	public final String valueOf(String name, String defaultValue) {
		return valueOf(name, defaultValue, defaultValue);
	}

	public final String valueOf(
			String name,
			String noValue,
			String defaultValue) {
		return valueOf(STRING_PARAMETER_TYPE, name, noValue, defaultValue);
	}

	/**
	 * Returns a nested parameters.
	 *
	 * <p>The nested parameters are parameters the same parameters, but with the
	 * names prepended by the given prefix:
	 * <ul>
	 * <li>If these parameters are top-level, then nested parameter names will
	 * start with the string {@code (prefix + ".")}.</li>
	 * <li>If these parameters are nested, then nested parameter names will
	 * start with the string {@code (thisPrefix + "." + prefix + ".")}.</li>
	 * </ul>
	 * </p>
	 *
	 * <p>If the nested parameter's name is empty, then the actual parameter
	 * name will be a prefix itself, without a trailing dot.</p>
	 *
	 * <p>The nested parameters are backed to these ones, i.e. all changes in
	 * nested parameters are immediately reflected in these ones, and vice
	 * versa.</p>
	 *
	 * @param prefix nested parameters prefix.
	 *
	 * @return nested parameters instance, or this parameters instance if the
	 * prefix is empty.
	 */
	public final Parameters sub(String prefix) {
		requireNonNull(prefix, "HTTP params prefix not specified");

		if (prefix.isEmpty()) {
			return this;
		}

		final ParametersStore store = store();
		final ParametersStore nested = store.nestedStore(prefix);

		if (nested != null) {
			return new Parameters(nested);
		}

		return new Parameters(new NestedParametersStore(store, prefix + '.'));
	}

	public final Parameters setBy(Parameterized parameterized) {
		parameterized.write(this);
		return this;
	}

	@SafeVarargs
	public final <T> Parameters set(
			ParameterType<T> type,
			String name,
			T... values) {
		requireNonNull(name, "Parameter name not specified");
		if (values == null) {
			store().setParam(name, null);
			return this;
		}
		if (values.length == 0) {
			store().setParam(name, new String[0]);
			return this;
		}
		type.setValues(this, name, values);
		return this;
	}

	@SafeVarargs
	public final <T> Parameters set(Parameter<T> parameter, T... values) {
		return set(parameter.getType(), parameter.getName(), values);
	}

	public final Parameters set(String name, String... values) {
		return set(STRING_PARAMETER_TYPE, name, values);
	}

	@SafeVarargs
	public final <T> Parameters add(
			ParameterType<T> type,
			String name,
			T... values) {
		requireNonNull(name, "Parameter name not specified");
		if (values == null) {
			return this;
		}
		type.addValues(this, name, values);
		return this;
	}

	@SafeVarargs
	public final <T> Parameters add(Parameter<T> parameter, T... values) {
		return add(parameter.getType(), parameter.getName(), values);
	}

	public final Parameters add(String name, String... values) {
		return add(STRING_PARAMETER_TYPE, name, values);
	}

	public Parameters urlDecode(Reader in) throws IOException {

		final StringBuilder name = new StringBuilder();
		final StringBuilder value = new StringBuilder();
		boolean hasValue = false;
		StringBuilder data = name;

		for (;;) {

			final int c = in.read();

			if (c < 0) {
				break;
			}

			switch (c) {
			case '&':
				addDecodedParam(name, value, hasValue);
				name.setLength(0);
				value.setLength(0);
				data = name;
				hasValue = false;
				break;
			case '=':
				hasValue = true;
				data = value;
				break;
			default:
				data.appendCodePoint(c);
			}
		}

		addDecodedParam(name, value, hasValue);

		return this;
	}

	@Override
	public String toString() {
		try {
			return urlEncode();
		} catch (UnsupportedEncodingException e) {
			return e.getMessage();
		}
	}

	private void addDecodedParam(
			StringBuilder name,
			StringBuilder value,
			boolean hasValue) {
		if (name.length() == 0 && !hasValue) {
			return;
		}
		if (!hasValue) {
			add(name.toString(), new String[0]);
			return;
		}
		add(name.toString(), value.toString());
	}

	/**
	 * URL-encodes these parameters into a string in UTF-8 encoding.
	 *
	 * @return a string containing URL-encoded data.
	 *
	 * @throws UnsupportedEncodingException if {@code UTF-8} encoding is not
	 * supported.
	 */
	public final String urlEncode() throws UnsupportedEncodingException {
		return urlEncode(UTF_8);
	}

	/**
	 * URL-encodes these parameters into a string.
	 *
	 * @param encoding encoding to use for URL encoding.
	 *
	 * @return a string containing URL-encoded data.
	 *
	 * @throws UnsupportedEncodingException if {@code encoding} is not
	 * supported.
	 */
	public final String urlEncode(
			String encoding)
	throws UnsupportedEncodingException {

		final StringBuilder out = new StringBuilder();

		try {
			urlEncode(out, encoding);
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (IOException e) {
			new IllegalStateException(e);// Should never be thrown.
		}

		return out.toString();
	}

	/**
	 * URL-encodes these parameters in UTF-8 encoding.
	 *
	 * @param out an appendable to write URL-encoded values to.
	 *
	 * @throws UnsupportedEncodingException if {@code UTF-8} encoding is not
	 * supported.
	 * @throws IOException if i/o exception happened during the attempt to write
	 * to the {@code appendable}.
	 */
	public final void urlEncode(
			Appendable out)
	throws UnsupportedEncodingException, IOException {
		urlEncode(out, UTF_8);
	}

	/**
	 * URL-encodes these parameters.
	 *
	 * @param out an appendable to write URL-encoded values to.
	 * @param encoding encoding to use for URL encoding.
	 *
	 * @throws UnsupportedEncodingException if {@code encoding} is not
	 * supported.
	 * @throws IOException if i/o exception happened during the attempt to write
	 * to the {@code appendable}.
	 *
	 */
	public void urlEncode(
			Appendable out,
			String encoding)
	throws UnsupportedEncodingException, IOException {

		boolean notFirst = false;

		for (Map.Entry<String, String[]> e : this.store) {

			final String name = URLEncoder.encode(e.getKey(), encoding);
			final String[] values = e.getValue();
			final int numVals = values.length;

			if (numVals == 0) {
				if (notFirst) {
					out.append('&');
				} else {
					notFirst = true;
				}
				out.append(name);
				continue;
			}

			for (int i = 0; i < numVals; ++i) {

				final String value = values[i];

				if (value == null) {
					continue;
				}
				if (notFirst) {
					out.append('&');
				} else {
					notFirst = true;
				}
				out.append(name).append('=');
				out.append(URLEncoder.encode(value, encoding));
			}
		}
	}

	@Override
	public final void read(Parameters params) {
		for (Map.Entry<String, String[]> e : params.store()) {
			set(e.getKey(), e.getValue());
		}
	}

	@Override
	public final void write(Parameters params) {
		params.read(this);
	}

}
