package ru.aplix.ltk.core.util;

import static java.lang.System.arraycopy;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;


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

	public final HttpParams set(String name, String... values) {
		this.store.putHttpParam(name, values);
		return this;
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

}
