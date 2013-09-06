package ru.aplix.ltk.core.util;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


final class ParametersMap implements ParametersStore {

	private final Map<String, String[]> map;

	ParametersMap(Map<String, String[]> map) {
		requireNonNull(map, "Parameters map not speicifed");
		this.map = map;
	}

	@Override
	public Iterator<Entry<String, String[]>> iterator() {
		return this.map.entrySet().iterator();
	}

	@Override
	public String[] getParam(String name) {
		return this.map.get(name);
	}

	@Override
	public String[] setParam(String name, String[] values) {
		return this.map.put(name, values);
	}

	@Override
	public ParametersStore nestedStore(String prefix) {
		return null;
	}

}
