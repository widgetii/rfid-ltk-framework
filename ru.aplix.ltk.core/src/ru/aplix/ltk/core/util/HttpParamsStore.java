package ru.aplix.ltk.core.util;

import java.util.Map;


public interface HttpParamsStore extends Iterable<Map.Entry<String, String[]>> {

	String[] getHttpParam(String name);

	String[] putHttpParam(String name, String[] values);

}
