package ru.aplix.ltk.core.util;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


final class HttpParamsMap implements HttpParamsStore {

	private final Map<String, String[]> map;

	HttpParamsMap(Map<String, String[]> map) {
		requireNonNull(map, "HTTP parameters map not speicifed");
		this.map = map;
	}

	@Override
	public Iterator<Entry<String, String[]>> iterator() {
		return this.map.entrySet().iterator();
	}

	@Override
	public String[] getHttpParam(String name) {
		return this.map.get(name);
	}

	@Override
	public String[] putHttpParam(String name, String[] values) {
		return this.map.put(name, values);
	}

}
