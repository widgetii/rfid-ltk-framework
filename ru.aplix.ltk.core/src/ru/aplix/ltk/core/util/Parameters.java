package ru.aplix.ltk.core.util;

import static java.lang.System.arraycopy;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public final class Parameters implements Parameterized {

	public static final String UTF_8 = "UTF-8";

	private final ParametersStore store;

	public Parameters() {
		this(new HashMap<String, String[]>());
	}

	public Parameters(int capacity) {
		this(new HashMap<String, String[]>(capacity));
	}

	public Parameters(Map<String, String[]> map) {
		this.store = new ParametersMap(map);
	}

	public Parameters(ParametersStore store) {
		requireNonNull(store, "Parameters store not specified");
		this.store = store;
	}

	public final ParametersStore store() {
		return this.store;
	}

	public final boolean have(String name) {
		return this.store.getParam(name) != null;
	}

	public final String[] valuesOf(String name, String... defaultValues) {

		final String[] values = store().getParam(name);

		return values != null ? values : defaultValues;
	}

	public final String[] valuesOf(
			String name,
			String[] noValues,
			String... defaultValues) {

		final String[] values = store().getParam(name);

		if (values == null) {
			return defaultValues;
		}
		if (values.length == 0) {
			return noValues;
		}

		return values;
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

		final String[] values = store().getParam(name);

		if (values == null) {
			return defaultValue;
		}
		if (values.length == 0) {
			return noValue;
		}

		return values[0];
	}

	public final int intValueOf(String name) {
		return intValueOf(name, 0, 0);
	}

	public final int intValueOf(String name, int defaultValue) {
		return intValueOf(name, defaultValue, defaultValue);
	}

	public final int intValueOf(String name, int noValue, int defaultValue) {

		final String value = valueOf(
				name,
				Long.toString(noValue),
				Long.toString(defaultValue));

		if (value == null) {
			return noValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return noValue;
		}
	}

	public final long longValueOf(String name) {
		return longValueOf(name, 0, 0);
	}

	public final long longValueOf(String name, long defaultValue) {
		return longValueOf(name, defaultValue, defaultValue);
	}

	public final long longValueOf(
			String name,
			long noValue,
			long defaultValue) {

		final String value = valueOf(
				name,
				Long.toString(noValue),
				Long.toString(defaultValue));

		if (value == null) {
			return noValue;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return noValue;
		}
	}

	public final <E extends Enum<E>> E enumValueOf(
			Class<E> enumType,
			String name) {
		return enumValueOf(enumType, name, null, null);
	}

	public final <E extends Enum<E>> E enumValueOf(
			Class<E> enumType,
			String name,
			E defaultValue) {
		return enumValueOf(enumType, name, defaultValue, defaultValue);
	}

	public <E extends Enum<E>> E enumValueOf(
			Class<E> enumType,
			String name,
			E noValue,
			E defaultValue) {

		final String[] values = store().getParam(name);

		if (values == null) {
			return defaultValue;
		}
		if (values.length == 0) {
			return noValue;
		}

		try {
			return Enum.valueOf(enumType, name);
		} catch (IllegalArgumentException e) {
			return noValue;
		}
	}

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

	public final Parameters set(String name, String... values) {
		this.store.setParam(name, values);
		return this;
	}

	public final Parameters set(String name, Object... values) {
		return set(name, strValues(values));
	}

	public final Parameters add(String name, Object... values) {
		return add(name, strValues(values));
	}

	public Parameters add(String name, String... values) {

		final String[] existing = this.store.setParam(name, values);

		if (existing == null) {
			return this;
		}
		if (values.length == 0) {
			this.store.setParam(name, existing);
			return this;
		}

		final String[] newValues = new String[existing.length + values.length];

		arraycopy(existing, 0, newValues, 0, existing.length);
		arraycopy(values, 0, newValues, existing.length, values.length);

		this.store.setParam(name, newValues);

		return this;
	}

	public final String urlEncode() throws UnsupportedEncodingException {
		return urlEncode(UTF_8);
	}

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

	public final void urlEncode(Appendable out) throws IOException {
		urlEncode(out, UTF_8);
	}

	public void urlEncode(Appendable out, String encoding) throws IOException {

		boolean notFirst = false;

		for (Map.Entry<String, String[]> e : this.store) {

			final String name = URLEncoder.encode(e.getKey(), encoding);
			final String[] values = e.getValue();
			final int numVals = values.length;

			if (notFirst) {
				out.append('&');
			} else {
				notFirst = true;
			}
			out.append(name);
			if (numVals == 0) {
				continue;
			}
			out.append('=').append(URLEncoder.encode(values[0], encoding));
			for (int i = 1; i < numVals; ++i) {

				final String value = values[i];

				out.append('&').append(name).append('=');
				if (value != null) {
					out.append(URLEncoder.encode(value, encoding));
				}
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

	private static String[] strValues(Object... values) {

		final String[] strValues = new String[values.length];

		for (int i = 0; i < values.length; ++i) {
			strValues[i] = Objects.toString(values[i], null);
		}

		return strValues;
	}

}
