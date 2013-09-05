package ru.aplix.ltk.collector.jetty;

import static org.eclipse.equinox.http.jetty.JettyConfigurator.startServer;
import static org.eclipse.equinox.http.jetty.JettyConfigurator.stopServer;
import static ru.aplix.ltk.collector.http.CollectorHttpConstants.COLLECTOR_HTTP_SERVICE_ID;

import java.util.Hashtable;

import org.eclipse.equinox.http.jetty.JettyConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

	private static final String PREFIX =
			Activator.class.getPackage().getName() + '.';
	private static final String PORT = PREFIX + "port";
	private static final String CONTEXT_PATH = PREFIX + "context.path";

	private static final String DEFAULT_CONTEXT_PATH = "/collector";


	@Override
	public void start(BundleContext context) throws Exception {
		startServer(COLLECTOR_HTTP_SERVICE_ID, settings(context));
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		stopServer(COLLECTOR_HTTP_SERVICE_ID);
	}

	private static Hashtable<String, Object> settings(BundleContext context) {

		final Hashtable<String, Object> settings = new Hashtable<>();

		setPort(context, settings);
		setContextPath(context, settings);

		return settings;
	}

	private static void setContextPath(
			BundleContext context,
			Hashtable<String, Object> settings) {

		String contextPath = context.getProperty(CONTEXT_PATH);

		if (contextPath == null) {
			contextPath = DEFAULT_CONTEXT_PATH;
		}

		settings.put(JettyConstants.CONTEXT_PATH, contextPath);
	}

	private static void setPort(
			BundleContext context,
			Hashtable<String, Object> settings) {

		final String port = context.getProperty(PORT);

		if (port == null) {
			return;
		}

		final Integer portNum;

		try {
			portNum = Integer.valueOf(port);
		} catch (NumberFormatException e) {
			return;
		}

		settings.put(JettyConstants.HTTP_PORT, portNum);
	}

}
