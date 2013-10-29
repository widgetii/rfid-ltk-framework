package ru.aplix.ltk.osgi;

import static java.util.Collections.emptyIterator;

import java.util.Iterator;

import org.osgi.framework.BundleContext;

import ru.aplix.ltk.core.util.ParametersStore;


final class BundleContextParameters implements ParametersStore {

	private final BundleContext context;

	BundleContextParameters(BundleContext context) {
		this.context = context;
	}

	@Override
	public Iterator<String> iterator() {
		return emptyIterator();
	}

	@Override
	public String[] getParam(String name) {

		final String property = this.context.getProperty(name);

		if (property == null) {
			return null;
		}

		return property.split(",");
	}

	@Override
	public String[] setParam(String name, String[] values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParametersStore nestedStore(String prefix) {
		return null;
	}

}
