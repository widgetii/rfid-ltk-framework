package ru.aplix.ltk.collector.http;

import static org.apache.http.client.utils.URLEncodedUtils.CONTENT_TYPE;
import static ru.aplix.ltk.core.util.Parameters.UTF_8;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


/**
 * An Apache HTTP entity representing the URL-encoded
 * {@link Parameters parameters}.
 */
public class ParametersEntity extends StringEntity {

	/**
	 * Construct URL-encoded parameters entity.
	 *
	 * @param parameters parameters to represent.
	 *
	 * @throws UnsupportedEncodingException if UTF-8 encoding is not supported.
	 */
	public ParametersEntity(
			Parameters parameters)
	throws UnsupportedEncodingException {
		super(parameters.urlEncode(), CONTENT_TYPE, UTF_8);
	}

	/**
	 * Constructs URL-encoded parameters entity filled with the data of
	 * the given object.
	 *
	 * @param parameterized target parameterized object.
	 *
	 * @throws UnsupportedEncodingException if UTF-8 encoding is not supported.
	 */
	public ParametersEntity(
			Parameterized parameterized)
	throws UnsupportedEncodingException {
		this(new Parameters().setBy(parameterized));
	}

}
