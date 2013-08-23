package ru.aplix.ltk.message.test;

import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.message.MsgSubscriptions;


public class TestSubscriptions extends MsgSubscriptions<TestHandle, String> {

	@Override
	protected TestHandle createHandle(
			MsgConsumer<? super TestHandle, ? super String> consumer) {
		return new TestHandle(this, consumer);
	}

}
