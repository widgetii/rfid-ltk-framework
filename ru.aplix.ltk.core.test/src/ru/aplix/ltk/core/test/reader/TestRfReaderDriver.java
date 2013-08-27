package ru.aplix.ltk.core.test.reader;

import ru.aplix.ltk.core.reader.*;


public class TestRfReaderDriver implements RfReaderDriver {

	private RfReaderContext context;

	@Override
	public String getRfPortId() {
		return "test";
	}

	@Override
	public void initRfReader(RfReaderContext context) {
		this.context = context;
	}

	@Override
	public void startRfReader() {
	}

	@Override
	public void stopRfReader() {
	}

	public void updateStatus(RfReaderStatusMessage status) {
		this.context.updateStatus(status);
	}

	public void sendData(RfDataMessage data) {
		this.context.sendData(data);
	}

}
