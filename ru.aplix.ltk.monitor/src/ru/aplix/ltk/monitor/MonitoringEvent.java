package ru.aplix.ltk.monitor;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;

import java.util.LinkedList;


public class MonitoringEvent {

	private final Monitoring monitoring;
	private final MonitoringSeverity severity;
	private final String name;
	private volatile Content content = new Content();
	private Timeout timeout;
	private Timeout parentTimeout;
	private LinkedList<MonitoringEventListener> listeners = new LinkedList<>();
	private boolean timedOut;

	public MonitoringEvent(Monitoring monitoring, MonitoringSeverity severity) {
		this(monitoring, severity, null);
	}

	public MonitoringEvent(
			Monitoring monitoring,
			MonitoringSeverity severity,
			String name) {
		requireNonNull(severity, "Monitoring severity not specified");
		this.monitoring = monitoring;
		this.severity = severity;
		this.name = name;
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

	public final String getName() {
		return this.name;
	}

	public final boolean isEventOccurred() {
		if (this.parentTimeout != null) {
			this.parentTimeout.parentEvent.checkTimeout();
		}
		return this.content.isEventOccurred();
	}

	public final Content getContent() {
		return this.content;
	}

	public final String getMessage() {
		return getContent().getMessage();
	}

	public final Throwable getCause() {
		return getContent().getCause();
	}

	public final long getTimeout() {
		return this.timeout != null ? this.timeout.timeout : 0;
	}

	public boolean isTimedOut() {
		if (this.timeout == null) {
			return false;
		}
		return checkTimeout();
	}

	public final MonitoringEvent afterTimeoutOccur(
			long timeout,
			MonitoringEvent event) {
		this.timeout = new Timeout(timeout, this, event, null, false);
		return forgetOnUpdate(event);
	}

	public final MonitoringEvent afterTimeoutOccur(
			long timeout,
			MonitoringEvent event,
			String message) {
		this.timeout = new Timeout(timeout, this, event, message, false);
		return forgetOnUpdate(event);
	}

	public final MonitoringEvent afterTimeoutActivate(
			long timeout,
			MonitoringEvent event) {
		this.timeout = new Timeout(timeout, this, event, null, true);
		return forgetOnUpdate(event);
	}

	public final MonitoringEvent afterTimeoutActivate(
			long timeout,
			MonitoringEvent event,
			String message) {
		this.timeout = new Timeout(timeout, this, event, message, true);
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
			public void eventOccurred(
					MonitoringEvent event,
					Content occurred) {
				eventToForget.forget();
			}
			@Override
			public void eventForgotten(
					MonitoringEvent event,
					Content forgotten) {
				eventToForget.forget();
			}
		});
	}

	public final MonitoringEvent forgetWhenForgot(
			final MonitoringEvent eventToForget) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(
					MonitoringEvent event,
					Content occurred) {
			}
			@Override
			public void eventForgotten(
					MonitoringEvent event,
					Content forgotten) {
				eventToForget.forget();
			}
		});
	}

	public final MonitoringEvent forgetWhenOccurred(
			final MonitoringEvent eventToForget) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(
					MonitoringEvent event,
					Content occurred) {
				eventToForget.forget();
			}
			@Override
			public void eventForgotten(
					MonitoringEvent event,
					Content forgotten) {
			}
		});
	}

	public final MonitoringEvent occurWhenForgot(
			final MonitoringEvent eventToOccur) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(
					MonitoringEvent event,
					Content occurred) {
				eventToOccur.forget();
			}
			@Override
			public void eventForgotten(
					MonitoringEvent event,
					Content forgotten) {
				eventToOccur.occur(forgotten, false, true);
			}
		});
	}

	public final MonitoringEvent occurWhenForgot(
			final MonitoringEvent eventToOccur,
			final String message) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(
					MonitoringEvent event,
					Content occurred) {
				eventToOccur.forget();
			}
			@Override
			public void eventForgotten(
					MonitoringEvent event,
					Content forgotten) {
				eventToOccur.occur(message);
			}
		});
	}

	public final MonitoringEvent activateWhenOccurred(
			final MonitoringEvent eventToOccur,
			final String message) {
		return addListener(new MonitoringEventListener() {
			@Override
			public void eventOccurred(
					MonitoringEvent event,
					Content occurred) {
				eventToOccur.activate(message);
			}
			@Override
			public void eventForgotten(
					MonitoringEvent event,
					Content forgotten) {
			}
		});
	}

	public final void occur(String message) {
		occur(message, null);
	}

	public final void occur(String message, Throwable cause) {
		occur(
				new Content(
						currentTimeMillis(),
						getMonitoring().nextEventId(),
						message,
						cause),
				false,
				true);
	}

	public final void activate(String message) {
		occur(
				new Content(
						currentTimeMillis(),
						getMonitoring().nextEventId(),
						message,
						null),
				true,
				true);
	}

	public final void forget() {

		final Content content = new Content();
		final Content forgotten;

		synchronized (this) {
			forgotten = this.content;
			if (!forgotten.isEventOccurred()) {
				return;
			}
			this.content = content;
			this.timedOut = false;
		}
		for (MonitoringEventListener listener : this.listeners) {
			synchronized (this) {
				if (this.content != content) {
					break;
				}
				listener.eventForgotten(this, forgotten);
			}
		}
	}

	public void report(MonitoringReport report) {

		final Content content = actualContent();

		if (content != null) {
			report.report(
					content.getTimestamp(),
					content.getEventId(),
					getSeverity(),
					content.getMessage(),
					content.getCause());
		}
	}

	@Override
	public String toString() {
		if (this.severity == null) {
			return super.toString();
		}
		if (this.name != null) {
			return this.name;
		}
		return getTarget() + " " + getSeverity();
	}

	protected void log(Content content) {
		getMonitoring().getLogger().log(
				null,
				getSeverity().getLogLevel(),
				getTarget() + " " + content.getMessage(),
				content.getCause());
	}

	private final void occur(
			Content content,
			boolean activate,
			boolean log) {
		synchronized (this) {
			if (activate && this.content.isEventOccurred()) {
				return;
			}
			this.timedOut = false;
			this.content = content;
		}
		if (log) {
			log(content);
		}
		for (MonitoringEventListener listener : this.listeners) {
			synchronized (this) {
				if (this.content != content) {
					break;
				}
				listener.eventOccurred(this, content);
			}
		}
	}

	private synchronized boolean checkTimeout() {
		if (this.timedOut) {
			return true;
		}

		final Content content = getContent();

		if (content == null) {
			return false;
		}
		if (content.getTimestamp() + getTimeout()
				> currentTimeMillis()) {
			return false;
		}
		this.timedOut = true;
		this.timeout.occur();

		return true;
	}

	private synchronized Content actualContent() {

		final Content content = getContent();

		if (!content.isEventOccurred()) {
			return null;
		}
		if (isTimedOut()) {
			return null;
		}

		return content;
	}

	public static final class Content {

		private final long timestamp;
		private final long eventId;
		private final String message;
		private final Throwable cause;
		private final boolean eventOccurred;

		Content(
				long timestamp,
				long eventId,
				String message,
				Throwable cause) {
			this.timestamp = timestamp;
			this.eventId = eventId;
			this.message = message;
			this.cause = cause;
			this.eventOccurred = true;
		}

		Content() {
			this.timestamp = 0;
			this.eventId = 0;
			this.message = null;
			this.cause = null;
			this.eventOccurred = false;
		}

		public final boolean isEventOccurred() {
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

	}

	private static final class Timeout {

		private final long timeout;
		private final MonitoringEvent parentEvent;
		private final MonitoringEvent event;
		private final String message;
		private final boolean activate;

		Timeout(
				long timeout,
				MonitoringEvent parentEvent,
				MonitoringEvent event,
				String message,
				boolean activate) {
			this.timeout = timeout;
			this.parentEvent = parentEvent;
			this.event = event;
			this.message = message;
			this.activate = activate;
			event.parentTimeout = this;
		}

		void occur() {
			if (this.message != null) {
				if (this.activate) {
					this.event.activate(this.message);
				} else {
					this.event.occur(this.message);
				}
			} else {
				this.event.occur(
						this.parentEvent.getContent(),
						this.activate,
						false);
			}
		}

	}

}
