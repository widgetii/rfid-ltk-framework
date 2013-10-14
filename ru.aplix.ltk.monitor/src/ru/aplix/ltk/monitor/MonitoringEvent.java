package ru.aplix.ltk.monitor;

import static java.lang.System.currentTimeMillis;

import java.util.LinkedList;


public class MonitoringEvent {

	private final Monitoring monitoring;
	private final MonitoringSeverity severity;
	private long timestamp;
	private String message;
	private Throwable cause;
	private Timeout timeout;
	private Timeout parentTimeout;
	private LinkedList<MonitoringEventListener> listeners = new LinkedList<>();
	private boolean eventOccurred;
	private boolean timedOut;

	public MonitoringEvent(
			Monitoring monitoring,
			MonitoringSeverity severity) {
		this.monitoring = monitoring;
		this.severity = severity;
		monitoring.addEvent(this);
	}

	public MonitoringTarget<?> getTarget() {
		return this.monitoring.getTarget();
	}

	public MonitoringSeverity getSeverity() {
		return this.severity;
	}

	public boolean isEventOccurred() {
		if (this.parentTimeout != null) {
			this.parentTimeout.parentEvent.isTimedOut();
		}
		return this.eventOccurred;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getMessage() {
		return this.message;
	}

	public Throwable getCause() {
		return this.cause;
	}

	public final long getTimeout() {
		return this.timeout.timeout;
	}

	public boolean isTimedOut() {
		if (this.timeout == null) {
			return false;
		}
		synchronized (this) {
			if (this.timedOut) {
				return true;
			}
			if (!isEventOccurred()) {
				return false;
			}
			if (getTimestamp() + getTimeout() > currentTimeMillis()) {
				return false;
			}
			this.timeout.occur();
			return this.timedOut = true;
		}
	}

	public final MonitoringEvent afterTimeout(
			long timeout,
			MonitoringEvent event) {
		this.timeout = new Timeout(timeout, this, event, null);
		return forgetWhenForgot(event);
	}

	public final MonitoringEvent afterTimeout(
			long timeout,
			MonitoringEvent event,
			String message) {
		this.timeout = new Timeout(timeout, this, event, message);
		return forgetWhenForgot(event);
	}

	public final MonitoringEvent addListener(
			MonitoringEventListener listener) {
		this.listeners.add(listener);
		return this;
	}

	public MonitoringEvent forgetWhenForgot(
			final MonitoringEvent eventToForget) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(MonitoringEvent event) {
			}
			@Override
			public void eventForgotten(MonitoringEvent event) {
				eventToForget.forget();
			}
		});
	}

	public MonitoringEvent forgetWhenOccurred(
			final MonitoringEvent eventToForget) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(MonitoringEvent event) {
				eventToForget.forget();
			}
			@Override
			public void eventForgotten(MonitoringEvent event) {
			}
		});
	}

	public MonitoringEvent occurWhenForgot(
			final MonitoringEvent eventToOccur) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(MonitoringEvent event) {
				eventToOccur.forget();
			}
			@Override
			public void eventForgotten(MonitoringEvent event) {
				eventToOccur.occur(getTimestamp(), getMessage(), getCause());
			}
		});
	}

	public MonitoringEvent occurWhenForgot(
			final MonitoringEvent eventToOccur,
			final String message) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(MonitoringEvent event) {
				eventToOccur.forget();
			}
			@Override
			public void eventForgotten(MonitoringEvent event) {
				eventToOccur.occur(getTimestamp(), message, null);
			}
		});
	}

	public final void occur(String message) {
		occur(message, null);
	}

	public final void occur(String message, Throwable cause) {
		occur(currentTimeMillis(), message, cause);
	}

	public void occur(long timestamp, String message, Throwable cause) {
		synchronized (this) {
			this.timedOut = false;
			this.eventOccurred = true;
			this.timestamp = timestamp;
			this.message = message;
			this.cause = cause;
		}
		for (MonitoringEventListener listener : this.listeners) {
			listener.eventOccurred(this);
		}
	}

	public final void forget() {
		synchronized (this) {
			this.eventOccurred = false;
		}
		for (MonitoringEventListener listener : this.listeners) {
			listener.eventForgotten(this);
		}
	}

	public synchronized void report(MonitoringReport report) {
		if (isEventOccurred() && !isTimedOut()) {
			report.report(
					getTimestamp(),
					getSeverity(),
					getMessage(),
					getCause());
		}
	}

	private static final class Timeout {

		private final long timeout;
		private final MonitoringEvent parentEvent;
		private final MonitoringEvent event;
		private final String message;

		Timeout(
				long timeout,
				MonitoringEvent parentEvent,
				MonitoringEvent event,
				String message) {
			this.timeout = timeout;
			this.parentEvent = parentEvent;
			this.event = event;
			event.parentTimeout = this;
			this.message = message;
		}

		void occur() {
			if (this.message != null) {
				this.event.occur(
						this.parentEvent.getTimestamp(),
						this.message,
						null);
			} else {
				this.event.occur(
						this.parentEvent.getTimestamp(),
						this.parentEvent.getMessage(),
						this.parentEvent.getCause());
			}
		}

	}

}
