package ru.aplix.ltk.core.reader;


public final class RfReaderContext {

	private final RfReader reader;

	RfReaderContext(RfReader reader) {
		this.reader = reader;
	}

	public void updateStatus(RfReaderStatusMessage statusMessage) {
		this.reader.readerSubscriptions().sendMessage(statusMessage);
	}

	public void sendTag(RfReadMessage tagMessage) {
		this.reader.tagSubscriptions().sendMessage(tagMessage);
	}

}
