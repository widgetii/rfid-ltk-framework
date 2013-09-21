package ru.aplix.ltk.core.util;

import java.net.MalformedURLException;
import java.net.URL;


final class URLParameterType extends SimpleParameterType<URL> {

	@Override
	public URL[] createValues(int length) {
		return new URL[length];
	}

	@Override
	public URL valueFromString(String string) {
		if (string.isEmpty()) {
			return null;
		}
		try {
			return new URL(string);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL", e);
		}
	}

	@Override
	public String valueToString(URL value) {
		return value.toExternalForm();
	}

}
