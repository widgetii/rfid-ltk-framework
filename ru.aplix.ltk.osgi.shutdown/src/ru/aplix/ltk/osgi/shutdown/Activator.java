package ru.aplix.ltk.osgi.shutdown;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;

import org.osgi.framework.*;
import org.osgi.framework.startlevel.FrameworkStartLevel;


/**
 * Registers {@link Runtime} shutdown hook.
 *
 * <p>When {@code SIGTERM} signal is sent to the process (e.g. with {@code kill}
 * command), it initiates a graceful OSGi shutdown.</p>
 *
 * <p>This bundle should be active in order to shutdown hook to work.</p>
 */
public class Activator
		implements BundleActivator, Runnable, FrameworkListener {

	private static final boolean DEBUG = false;
	private static final long SHUTDOWN_DELAY = 13000L;

	private final Thread hook = new Thread(this);
	private BundleContext context;
	private volatile boolean hookInProgress;
	private boolean stopped;

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		if (DEBUG) {
			System.err.println("[SHUTDOWN] Add hook");
		}
		this.hookInProgress = false;

		final Bundle systemBundle = this.context.getBundle(0);

		systemBundle.getBundleContext().addFrameworkListener(this);
		getRuntime().addShutdownHook(this.hook);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (this.hookInProgress) {
			if (DEBUG) {
				System.err.println("[SHUTDOWN] Hook in progess");
			}
			return;
		}
		if (DEBUG) {
			System.err.println("[SHUTDOWN] REMOVE HOOK");
		}
		getRuntime().removeShutdownHook(this.hook);

		final Bundle systemBundle = this.context.getBundle(0);

		systemBundle.getBundleContext().removeFrameworkListener(this);
	}

	@Override
	public void frameworkEvent(FrameworkEvent event) {
		if (!this.hookInProgress) {
			return;
		}
		if (isStopped(event)) {
			stopped();
		}
	}

	private boolean isStopped(FrameworkEvent event) {

		final int eventType = event.getType();

		if (DEBUG) {
			System.err.println("[SHUTDOWN] Framework event: " + eventType);
		}

		switch (eventType) {
		case FrameworkEvent.STOPPED:
			System.err.println("[SHUTDOWN] Framework stopped");
			return true;
		case FrameworkEvent.STARTLEVEL_CHANGED:

			final FrameworkStartLevel startLevel =
					event.getBundle().adapt(FrameworkStartLevel.class);
			final int level = startLevel.getStartLevel();

			if (DEBUG) {
				System.err.println(
						"[SHUTDOWN] Framework start level: " + level);
			}
			if (level == 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void run() {
		this.hookInProgress = true;

		System.err.println("[SHUTDOWN] Initiated");

		final Bundle systemBundle = this.context.getBundle(0);

		if (systemBundle != null) {
			try {
				systemBundle.stop();
				waitForShutdown();
			} catch (BundleException e) {
				System.err.println(
						"[SHUTDOWN] Failed to gracefully stop OSGi");
				e.printStackTrace();
				return;
			}
		}
	}

	private synchronized void stopped() {
		this.stopped = true;
		notifyAll();
	}

	private void waitForShutdown() {

		final long end = currentTimeMillis() + SHUTDOWN_DELAY;
		long left = SHUTDOWN_DELAY;

		for (;;) {
			synchronized (this) {
				if (this.stopped) {
					System.err.println("[SHUTDOWN] Succeed");
					return;
				}
				if (left <= 0) {
					System.err.println("[SHUTDOWN] Timed out");
					return;
				}
				try {
					wait(left);
				} catch (InterruptedException e) {
					System.err.println("[SHUTDOWN] Interrupted");
					return;
				}
				left = end - currentTimeMillis();
			}
		}
	}

}
