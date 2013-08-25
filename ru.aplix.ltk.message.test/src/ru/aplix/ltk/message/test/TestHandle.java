package ru.aplix.ltk.message.test;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgHandle;


public class TestHandle extends MsgHandle<TestHandle, String> {

	TestHandle(
			TestSubscriptions subscriptions,
			MsgConsumer<? super TestHandle, ? super String> consumer) {
		super(subscriptions, consumer);
	}

}
