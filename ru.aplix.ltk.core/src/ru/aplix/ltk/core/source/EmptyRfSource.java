package ru.aplix.ltk.core.source;


final class EmptyRfSource implements RfSource {

	@Override
	public void requestRfStatus(RfStatusUpdater updater) {
	}

	@Override
	public void requestRfData(RfDataReceiver receiver) {
	}

	@Override
	public void rejectRfData() {
	}

	@Override
	public void rejectRfStatus() {
	}

}
