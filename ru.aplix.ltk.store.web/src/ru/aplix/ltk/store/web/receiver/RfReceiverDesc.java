package ru.aplix.ltk.store.web.receiver;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.store.RfReceiver;


public final class RfReceiverDesc extends AbstractRfReceiverBean {

	public static RfReceiverDesc rfReceiverDesc(RfReceiver<?> receiver) {
		return rfReceiverDesc(receiver.getId(), receiver);
	}

	public static RfReceiverDesc rfReceiverDesc(
			int receiverId,
			RfReceiver<?> receiver) {
		if (receiver == null) {
			return new RfReceiverDesc(receiverId);
		}
		if (receiver.getRfProvider().getSettingsType()
				!= HttpRfSettings.class) {
			return new RfReceiverDesc(receiver, false);
		}

		@SuppressWarnings("unchecked")
		final RfReceiver<HttpRfSettings> httpReceiver =
				(RfReceiver<HttpRfSettings>) receiver;

		return new RfReceiverDesc(httpReceiver);
	}

	private final boolean deleted;

	public RfReceiverDesc() {
		this.deleted = false;
	}

	public RfReceiverDesc(RfReceiver<HttpRfSettings> receiver) {
		super(receiver);
		this.deleted = false;
	}

	private RfReceiverDesc(RfReceiver<?> receiver, boolean http) {
		super(receiver, http);
		this.deleted = false;
	}

	private RfReceiverDesc(int id) {
		super(id);
		this.deleted = true;
	}

	public final boolean isDeleted() {
		return this.deleted;
	}

}
