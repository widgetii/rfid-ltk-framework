package ru.aplix.ltk.collector.http;

import static java.lang.System.arraycopy;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public final class HttpParams {

	private final Map<String, String[]> params;

	public HttpParams(Map<String, String[]> params) {
		this.params = params;
	}

	public HttpParams(int capacity) {
		this.params = new HashMap<>(capacity);
	}

	public final Map<String, String[]> map() {
		return this.params;
	}

	public final HttpParams set(String name, String... values) {
		this.params.put(name, values);
		return this;
	}

	public final boolean have(String name) {
		return this.params.containsKey(name);
	}

	public final String[] valuesOf(String name, String... defaultValues) {

		final String[] values = this.params.get(name);

		return values != null ? values : defaultValues;
	}

	public final String valueOf(String name) {
		return valueOf(name, null);
	}

	public final String valueOf(String name, String defaultValue) {

		final String[] values = this.params.get(name);

		if (values == null || values.length == 0) {
			return defaultValue;
		}

		return values[0];
	}

	public HttpParams add(String name, String... values) {

		final String[] existing = this.params.put(name, values);

		if (existing == null) {
			return this;
		}
		if (values.length == 0) {
			this.params.put(name, existing);
			return this;
		}

		final String[] newValues = new String[existing.length + values.length];

		arraycopy(existing, 0, newValues, 0, existing.length);
		arraycopy(values, 0, newValues, existing.length, values.length);

		this.params.put(name, newValues);

		return this;
	}

	public void encode(Appendable out, String encoding) throws IOException {

		boolean notFirst = false;

		for (Map.Entry<String, String[]> e : map().entrySet()) {

			final String[] values = e.getValue();
			final int numVals = values.length;

			if (numVals == 0) {
				continue;
			}

			final String name = URLEncoder.encode(e.getKey(), encoding);

			if (notFirst) {
				out.append('&');
			} else {
				notFirst = true;
			}

			out.append(name);
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
