package ru.aplix.ltk.core.util;

import java.util.Map;


public interface ParametersStore extends Iterable<Map.Entry<String, String[]>> {

	String[] getParam(String name);

	String[] setParam(String name, String[] values);

}
