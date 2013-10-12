package ru.aplix.ltk.core.util;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Executor that executes tasks sequentially in dedicated thread.
 *
 * <p>If a task can not be submitted, the client code waits until it became
 * possible.</p>
 */
public class SingleThreadExecutor implements Executor, Closeable {

	private static final Runnable STOP = new Runnable() {
		@Override
		public void run() {
			throw new IllegalStateException("Stopped");
		}
		@Override
		public String toString() {
			return "STOP";
		}
	};

	private final BlockingQueue<Runnable> tasks;
	private final Thread thread;
	private volatile boolean shutdown;

	/**
	 * Constructs single thread executor with the given tasks queue.
	 *
	 * @param tasks the queue containing tasks.
	 */
	public SingleThreadExecutor(BlockingQueue<Runnable> tasks) {
		requireNonNull(tasks, "Tasks queue not specified");
		this.tasks = tasks;
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				runTasks();
			}
		});
		this.thread.start();
	}

	/**
	 * Construct single thread executor with the given tasks queue capacity.
	 *
	 * @param capacity maximum capacity of the tasks queue.
	 */
	public SingleThreadExecutor(int capacity) {
		this(new LinkedBlockingQueue<Runnable>(capacity));
	}

	/**
	 * The thread the tasks are running by.
	 *
	 * @return executor's thread.
	 */
	public final Thread getThread() {
		return this.thread;
	}

	/**
	 * Tasks queue.
	 *
	 * @return the queue passed to constructor.
	 */
	public final BlockingQueue<Runnable> getTasks() {
		return this.tasks;
	}

	/**
	 * Whether this executor is shut down.
	 *
	 * @return <code>true</code> if {@link #shutdown(boolean)} is called, or
	 * <code>false</code> otherwise.
	 */
	public final boolean isShutdown() {
		return this.shutdown;
	}

	/**
	 * Offers a task for execution.
	 *
	 * <p>The execution may fail if this executor is shut down, or if the tasks
	 * queue is full.</p>
	 *
	 * @param task the task to execute.
	 *
	 * @return <code>true</code> if the task has been submitted for execution,
	 * or <code>false</code> otherwise.
	 */
	public boolean offer(Runnable task) {
		if (isShutdown()) {
			return false;
		}
		return this.tasks.offer(task);
	}

	/**
	 * Submits a task for execution.
	 *
	 * <p>If the tasks queue is full, this method waits until it became possible
	 * to submit the task.
	 *
	 * @param task the task to execute.
	 *
	 * @return <code>true</code> if the task has been submitted for execution,
	 * or <code>false</code> if executor is shut down.
	 */
	public boolean submit(Runnable task) {
		if (isShutdown()) {
			return false;
		}
		try {
			this.tasks.put(task);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	@Override
	public final void execute(Runnable command) {
		submit(command);
	}

	/**
	 * Shuts executor down.
	 *
	 * <p>Does nothing if executor is already shut down.</p>
	 *
	 * @param interrupt whether to interrupt the running task execution thread.
	 */
	public void shutdown(boolean interrupt) {
		if (isShutdown()) {
			return;
		}
		this.shutdown = true;
		this.tasks.clear();
		this.tasks.add(STOP);
		if (interrupt) {
			this.thread.interrupt();
		}
	}

	/**
	 * Shuts down the executor without interrupting the task execution thread.
	 */
	@Override
	public void close() {
		shutdown(false);
	}

	@Override
	public String toString() {
		if (this.tasks == null) {
			return super.toString();
		}

		final StringBuilder out = new StringBuilder();

		out.append("SingleThreadExecutor[");
		if (isShutdown()) {
			out.append("shutdown");
		} else {
			out.append("scheduled: ").append(this.tasks.size());
		}
		out.append(']');

		return out.toString();
	}

	@Override
	protected void finalize() throws Throwable {
		shutdown(true);
	}

	private void runTasks() {
		for (;;) {

			final Runnable task;

			try {
				task = this.tasks.take();
			} catch (InterruptedException e) {
				return;
			}
			if (task == STOP) {
				return;
			}
			try {
				task.run();
			} catch (Throwable e) {

				final Thread thread = getThread();
				final UncaughtExceptionHandler handler =
						thread.getUncaughtExceptionHandler();

				if (handler != null) {
					handler.uncaughtException(thread, e);
				}
			}
		}
	}

}
