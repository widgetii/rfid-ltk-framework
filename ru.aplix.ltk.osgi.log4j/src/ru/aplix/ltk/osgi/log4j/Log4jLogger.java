package ru.aplix.ltk.osgi.log4j;

import static org.apache.log4j.LogManager.getLogger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.*;
import org.osgi.util.tracker.ServiceTracker;


public class Log4jLogger implements BundleActivator, LogListener {

	private LogReaderTracker tracker;

	@Override
	public void start(BundleContext context) throws Exception {
		this.tracker = new LogReaderTracker(context);
		this.tracker.open();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		this.tracker.close();
	}

	@Override
	public void logged(LogEntry entry) {

		final Logger logger = getLogger(entry.getBundle().getSymbolicName());

		logger.log(
				log4jLevel(entry.getLevel()),
				entry.getMessage(),
				entry.getException());
	}

	private static Level log4jLevel(int level) {
		switch (level) {
		case LogService.LOG_DEBUG:
			return Level.DEBUG;
		case LogService.LOG_INFO:
			return Level.INFO;
		case LogService.LOG_WARNING:
			return Level.WARN;
		case LogService.LOG_ERROR:
			return Level.ERROR;
		}
		return Level.toLevel(level, Level.ERROR);
	}

	private final class LogReaderTracker
			extends ServiceTracker<LogReaderService, LogReaderService> {

		LogReaderTracker(BundleContext context) {
			super(context, LogReaderService.class, null);
		}

		@Override
		public LogReaderService addingService(
				ServiceReference<LogReaderService> reference) {

			final LogReaderService service = super.addingService(reference);

			service.addLogListener(Log4jLogger.this);

			return service;
		}

		@Override
		public void removedService(
				ServiceReference<LogReaderService> reference,
				LogReaderService service) {
			try {
				service.removeLogListener(Log4jLogger.this);
			} finally {
				super.removedService(reference, service);
			}
		}

	}

}
