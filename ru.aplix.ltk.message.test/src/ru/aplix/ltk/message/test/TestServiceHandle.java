package ru.aplix.ltk.message.test;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgServiceHandle;


public class TestServiceHandle
		extends MsgServiceHandle<TestServiceHandle, String> {

	private final TestService testService;

	TestServiceHandle(
			TestService service,
			MsgConsumer<? super TestServiceHandle, ? super String> consumer) {
		super(service, consumer);
		this.testService = service;
	}

	public final TestHandle subscribeOn1(TestConsumer consumer) {
		return addSubscription(
				this.testService.subscriptions1().subscribe(consumer));
	}

	public final TestHandle subscribeOn2(TestConsumer consumer) {
		return addSubscription(
				this.testService.subscriptions2().subscribe(consumer));
	}

}
