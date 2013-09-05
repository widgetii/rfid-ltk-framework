package ru.aplix.ltk.collector.http.server;

import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.service.log.LogService.*;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;


public class Logger extends ServiceTracker<LogService, LogService> {

	public Logger(BundleContext context) {
		super(context, LogService.class, null);
	}

	public final void info(String message) {
		log(null, LOG_INFO, message, null);
	}

	public final void debug(String message) {
		log(null, LOG_DEBUG, message, null);
	}

	public final void warning(String message) {
		log(null, LOG_WARNING, message, null);
	}

	public final void error(String message) {
		log(null, LOG_ERROR, message, null);
	}

	public final void error(String message, Throwable cause) {
		log(null, LOG_ERROR, message, cause);
	}

	public final void info(
			ServiceReference<?> serviceReference,
			String message) {
		log(serviceReference, LOG_INFO, message, null);
	}

	public final void debug(
			ServiceReference<?> serviceReference,
			String message) {
		log(serviceReference, LOG_DEBUG, message, null);
	}

	public final void warning(
			ServiceReference<?> serviceReference,
			String message) {
		log(serviceReference, LOG_WARNING, message, null);
	}

	public final void error(
			ServiceReference<?> serviceReference,
			String message) {
		log(serviceReference, LOG_ERROR, message, null);
	}

	public final void error(
			ServiceReference<?> serviceReference,
			String message,
			Throwable cause) {
		log(serviceReference, LOG_ERROR, message, cause);
	}

	public void log(
			ServiceReference<?> serviceReference,
			int level,
			String message,
			Throwable cause) {

		final LogService service = getService();

		if (service != null) {
			service.log(serviceReference, level, message, cause);
		} else {
			printMessage(serviceReference, level, message, cause);
		}
	}

	protected void printMessage(
			ServiceReference<?> serviceReference,
			int level,
			String message,
			Throwable cause) {

		final StringBuilder out = new StringBuilder();

		out.append(level(level));
		if (serviceReference != null) {
			out.append('(');
			out.append(serviceReference.getProperty(SERVICE_ID));
			out.append("): ");
		} else {
			out.append(": ");
		}
		if (message != null) {
			out.append(message);
		}
		if (cause == null) {
			System.err.println(out);
		} else {
			if (message != null) {
				System.err.println(out);
			} else {
				System.err.print(out);
			}
			cause.printStackTrace();
		}
	}

	private static String level(int level) {
		switch (level) {
		case LOG_DEBUG:
			return "DEBUG";
		case LOG_INFO:
			return "INFO";
		case LOG_WARNING:
			return "WARNING";
		case LOG_ERROR:
			return "ERROR";
		default:
			return "ERROR?";
		}
	}

}
