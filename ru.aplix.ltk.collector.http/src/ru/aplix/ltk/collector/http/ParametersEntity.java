package ru.aplix.ltk.collector.http;

import static org.apache.http.client.utils.URLEncodedUtils.CONTENT_TYPE;
import static ru.aplix.ltk.core.util.Parameters.UTF_8;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class ParametersEntity extends StringEntity {

	public ParametersEntity(
			Parameters parameters)
	throws UnsupportedEncodingException {
		super(parameters.urlEncode(), CONTENT_TYPE, UTF_8);
	}

	public ParametersEntity(
			Parameterized parameterized)
	throws UnsupportedEncodingException {
		this(new Parameters().setBy(parameterized));
	}

}
