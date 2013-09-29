package ru.aplix.ltk.collector.http.server;

import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;
import ru.aplix.ltk.osgi.Logger;


final class ClrAutostart
		implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

	private static final DummyTagConsumer DUMMY_TAG_CONSUMER =
			new DummyTagConsumer();

	private final ClrProfile<?> profile;
	private RfCollectorHandle handle;

	public ClrAutostart(ClrProfile<?> profile) {
		this.profile = profile;
	}

	public void start() {
		log().debug("Starting " + this.profile);
		this.profile.connect().getCollector().subscribe(this);
	}

	@Override
	public void consumerSubscribed(RfCollectorHandle handle) {
		this.handle = handle;
		log().info("Started " + this.profile);
		handle.requestTagAppearance(DUMMY_TAG_CONSUMER);
	}

	@Override
	public void messageReceived(RfStatusMessage message) {
		if (message.getRfStatus().isError()) {
			log().error(message.getErrorMessage());
		}
	}

	@Override
	public void consumerUnsubscribed(RfCollectorHandle handle) {
		this.handle = null;
		log().info("Stopped " + this.profile);
	}

	public void stop() {
		if (this.handle != null) {
			log().debug("Stopping " + this);
			this.handle.unsubscribe();
		}
	}

	private final Logger log() {
		return this.profile.log();
	}

	private static final class DummyTagConsumer
			implements MsgConsumer<
					RfTagAppearanceHandle,
					RfTagAppearanceMessage> {

		@Override
		public void consumerSubscribed(RfTagAppearanceHandle handle) {
		}

		@Override
		public void messageReceived(RfTagAppearanceMessage message) {
		}

		@Override
		public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
		}

	}

}
