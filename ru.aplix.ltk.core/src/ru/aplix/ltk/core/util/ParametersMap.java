package ru.aplix.ltk.core.util;

import java.util.Iterator;
import java.util.Map;


final class ParametersMap implements ParametersStore {

	private final Map<String, String[]> map;

	ParametersMap(Map<String, String[]> map) {
		this.map = map;
	}

	@Override
	public Iterator<String> iterator() {
		return this.map.keySet().iterator();
	}

	@Override
	public String[] getParam(String name) {
		return this.map.get(name);
	}

	@Override
	public String[] setParam(String name, String[] values) {
		if (values == null) {
			return this.map.remove(name);
		}
		return this.map.put(name, values);
	}

	@Override
	public ParametersStore nestedStore(String prefix) {
		return null;
	}

}
