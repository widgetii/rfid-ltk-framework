package ru.aplix.ltk.collector.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class ClrClientRequest implements Parameterized {

	private URL clientURL;

	public ClrClientRequest(Parameters params) {
		read(params);
	}

	public URL getClientURL() {
		return this.clientURL;
	}

	public void setClientURL(URL clientURL) {
		if (clientURL != null) {

			final String protocol = clientURL.getProtocol();

			if (!"http".equals(protocol) && !"https".equals(protocol)) {
				throw new IllegalArgumentException(
						"HTTP/HTTPS client URL expected: " + clientURL);
			}
		}

		this.clientURL = clientURL;
	}

	@Override
	public void read(Parameters params) {
		setClientURL(clientURLFromParams(params));
	}

	@Override
	public void write(Parameters params) {
		params.set("clientURL", getClientURL());
	}

	private URL clientURLFromParams(Parameters params) {
		try {

			final String url = params.valueOf(
					"clientURL",
					null,
					Objects.toString(getClientURL(), null));

			return url != null ? new URL(url) : null;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid client URL", e);
		}
	}

}
