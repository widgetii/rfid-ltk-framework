package ru.aplix.ltk.monitor;

import static java.lang.System.currentTimeMillis;

import java.util.LinkedList;


public class MonitoringEvent {

	private final Monitoring monitoring;
	private final MonitoringSeverity severity;
	private long timestamp;
	private long eventId;
	private String message;
	private Throwable cause;
	private Timeout timeout;
	private Timeout parentTimeout;
	private LinkedList<MonitoringEventListener> listeners = new LinkedList<>();
	private boolean eventOccurred;
	private boolean timedOut;

	public MonitoringEvent(Monitoring monitoring, MonitoringSeverity severity) {
		this.monitoring = monitoring;
		this.severity = severity;
		monitoring.addEvent(this);
	}

	public final Monitoring getMonitoring() {
		return this.monitoring;
	}

	public final MonitoringTarget<?> getTarget() {
		return getMonitoring().getTarget();
	}

	public final MonitoringSeverity getSeverity() {
		return this.severity;
	}

	public final boolean isEventOccurred() {
		if (this.parentTimeout != null) {
			this.parentTimeout.parentEvent.isTimedOut();
		}
		return this.eventOccurred;
	}

	public final long getTimestamp() {
		return this.timestamp;
	}

	public final long getEventId() {
		return this.eventId;
	}

	public final String getMessage() {
		return this.message;
	}

	public final Throwable getCause() {
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
			this.timedOut = true;
			this.timeout.occur();
			return true;
		}
	}

	public final MonitoringEvent afterTimeout(
			long timeout,
			MonitoringEvent event) {
		this.timeout = new Timeout(timeout, this, event, null);
		return forgetOnUpdate(event);
	}

	public final MonitoringEvent afterTimeout(
			long timeout,
			MonitoringEvent event,
			String message) {
		this.timeout = new Timeout(timeout, this, event, message);
		return forgetOnUpdate(event);
	}

	public final MonitoringEvent addListener(
			MonitoringEventListener listener) {
		this.listeners.add(listener);
		return this;
	}

	public final MonitoringEvent forgetOnUpdate(
			final MonitoringEvent eventToForget) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(MonitoringEvent event) {
				eventToForget.forget();
			}
			@Override
			public void eventForgotten(MonitoringEvent event) {
				eventToForget.forget();
			}
		});
	}

	public final MonitoringEvent forgetWhenForgot(
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

	public final MonitoringEvent forgetWhenOccurred(
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

	public final MonitoringEvent occurWhenForgot(
			final MonitoringEvent eventToOccur) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(MonitoringEvent event) {
				eventToOccur.forget();
			}
			@Override
			public void eventForgotten(MonitoringEvent event) {
				eventToOccur.occur(
						getTimestamp(),
						getMessage(),
						getCause());
			}
		});
	}

	public final MonitoringEvent occurWhenForgot(
			final MonitoringEvent eventToOccur,
			final String message) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(MonitoringEvent event) {
				eventToOccur.forget();
			}
			@Override
			public void eventForgotten(MonitoringEvent event) {
				eventToOccur.occur(message);
			}
		});
	}

	public final void occur(String message) {
		occur(message, null);
	}

	public final void occur(String message, Throwable cause) {
		occur(currentTimeMillis(), message, cause, true);
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
					getEventId(),
					getSeverity(),
					getMessage(),
					getCause());
		}
	}

	protected final void occur(
			long timestamp,
			String message,
			Throwable cause) {
		occur(timestamp, message, cause, false);
	}

	protected void occur(
			long timestamp,
			String message,
			Throwable cause,
			boolean log) {

		final long eventId = getMonitoring().nextEventId();

		synchronized (this) {
			this.timedOut = false;
			this.eventOccurred = true;
			this.timestamp = timestamp;
			this.eventId = eventId;
			this.message = message;
			this.cause = cause;
			if (log) {
				log();
			}
		}
		for (MonitoringEventListener listener : this.listeners) {
			listener.eventOccurred(this);
		}
	}

	protected void log() {
		getMonitoring().getLogger().log(
				null,
				getSeverity().getLogLevel(),
				getTarget() + " " + getMessage(),
				getCause());
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
				this.event.occur(this.message);
			} else {
				this.event.occur(
						this.parentEvent.getTimestamp(),
						this.parentEvent.getMessage(),
						this.parentEvent.getCause());
			}
		}

	}

}
