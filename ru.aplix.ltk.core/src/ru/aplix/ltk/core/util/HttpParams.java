package ru.aplix.ltk.core.util;

import static java.lang.System.arraycopy;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;


public final class HttpParams {

	private final HttpParamsStore store;

	public HttpParams(HttpParamsStore store) {
		requireNonNull(store, "HTTP parameters store not specified");
		this.store = store;
	}

	public HttpParams(Map<String, String[]> map) {
		this.store = new HttpParamsMap(map);
	}

	public final HttpParamsStore store() {
		return this.store;
	}

	public final boolean have(String name) {
		return this.store.getHttpParam(name) != null;
	}

	public final String[] valuesOf(String name, String... defaultValues) {

		final String[] values = this.store.getHttpParam(name);

		return values != null ? values : defaultValues;
	}

	public final String[] valuesOf(
			String name,
			String[] noValues,
			String... defaultValues) {

		final String[] values = this.store.getHttpParam(name);

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

		final String[] values = this.store.getHttpParam(name);

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

	public final HttpParams sub(String prefix) {
		requireNonNull(prefix, "HTTP params prefix not specified");
		return new HttpParams(new NestedHttpParams(store(), prefix));
	}

	public final HttpParams set(String name, String... values) {
		this.store.putHttpParam(name, values);
		return this;
	}

	public final HttpParams set(String name, Object... values) {
		return set(name, strValues(values));
	}

	public final HttpParams add(String name, Object... values) {
		return add(name, strValues(values));
	}

	public HttpParams add(String name, String... values) {

		final String[] existing = this.store.putHttpParam(name, values);

		if (existing == null) {
			return this;
		}
		if (values.length == 0) {
			this.store.putHttpParam(name, existing);
			return this;
		}

		final String[] newValues = new String[existing.length + values.length];

		arraycopy(existing, 0, newValues, 0, existing.length);
		arraycopy(values, 0, newValues, existing.length, values.length);

		this.store.putHttpParam(name, newValues);

		return this;
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

	private static String[] strValues(Object... values) {

		final String[] strValues = new String[values.length];

		for (int i = 0; i < values.length; ++i) {
			strValues[i] = Objects.toString(values[i], null);
		}

		return strValues;
	}

}
