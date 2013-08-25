package ru.aplix.ltk.message.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgService;


public class TestService extends MsgService<TestServiceHandle, String> {

	private boolean started;
	private boolean stopped;
	private final TestSubscriptions subscriptions1 = new TestSubscriptions();
	private final TestSubscriptions subscriptions2 = new TestSubscriptions();

	public final boolean isStarted() {
		return this.started;
	}

	public final boolean isStopped() {
		return this.stopped;
	}

	public final TestSubscriptions subscriptions1() {
		return this.subscriptions1;
	}

	public final TestSubscriptions subscriptions2() {
		return this.subscriptions2;
	}

	@Override
	protected TestServiceHandle createServiceHandle(
			MsgConsumer<? super TestServiceHandle, ? super String> consumer) {
		return new TestServiceHandle(this, consumer);
	}

	@Override
	protected void startService() {
		assertThat(this.started, is(false));
		this.started = true;
		this.stopped = false;
	}

	@Override
	protected void stopService() {
		assertThat(this.stopped, is(false));
		this.stopped = true;
		this.started = false;
	}

}
