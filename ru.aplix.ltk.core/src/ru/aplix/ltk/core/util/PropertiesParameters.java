package ru.aplix.ltk.core.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;


final class PropertiesParameters implements ParametersStore {

	private final Properties properties;

	PropertiesParameters(Properties properties) {
		this.properties = properties;
	}

	@Override
	public Iterator<String> iterator() {
		return this.properties.stringPropertyNames().iterator();
	}

	@Override
	public String[] getParam(String name) {
		return decodeProperty(this.properties.getProperty(name));
	}

	@Override
	public String[] setParam(String name, String[] values) {

		final String newProperty;

		if (values == null) {
			newProperty = null;
		} else if (values.length == 0) {
			newProperty = "";
		} else if (values.length == 1) {

			final String value = values[0];

			newProperty = value == null ? null : escapeValue(value);
		} else {

			final StringBuilder result = new StringBuilder();
			boolean hasValues = false;

			for (String value : values) {
				if (value == null) {
					continue;
				}
				if (hasValues) {
					result.append(',');
				} else {
					hasValues = true;
				}
				result.append(escapeValue(value));
			}

			if (!hasValues) {
				newProperty = null;
			} else {
				newProperty = result.toString();
			}
		}

		final String oldProperty;

		if (newProperty == null) {
			oldProperty = (String) this.properties.remove(name);
		} else {
			oldProperty =
					(String) this.properties.setProperty(name, newProperty);
		}

		return decodeProperty(oldProperty);
	}

	@Override
	public ParametersStore nestedStore(String prefix) {
		return null;
	}

	private static String escapeValue(String value) {
		return value.replaceAll(",", "\\,");
	}

	private static String[] decodeProperty(String property) {
		if (property == null) {
			return null;
		}

		final int len = property.length();
		final LinkedList<String> values = new LinkedList<>();
		final StringBuilder value = new StringBuilder(len);
		boolean escape = false;

		for (int i = 0; i < len; ++i) {

			final char c = property.charAt(i);

			if (escape) {
				escape = false;
				value.append(c);
				continue;
			}
			if (c == '\\') {
				escape = true;
				continue;
			}
			if (c == ',') {
				values.add(value.toString());
				value.setLength(0);
				continue;
			}
			value.append(c);
		}

		return values.toArray(new String[values.size()]);
	}

}
