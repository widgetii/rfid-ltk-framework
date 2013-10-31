package ru.aplix.ltk.store.web.receiver;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfReceiverEditor;


public class RfReceiverDesc {

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

		return new RfReceiverBean(httpReceiver);
	}

	private int id;
	private String remoteURL;
	private final boolean deleted;

	public RfReceiverDesc() {
		this.deleted = false;
	}

	public RfReceiverDesc(RfReceiver<HttpRfSettings> receiver) {
		this(receiver, true);

		final RfReceiverEditor<HttpRfSettings> editor = receiver.modify();

		this.remoteURL = editor.getRfSettings().getCollectorURL().toString();
	}

	private RfReceiverDesc(
			RfReceiver<?> receiver,
			@SuppressWarnings("unused") boolean http) {
		this.id = receiver.getId();
		this.deleted = false;
	}

	private RfReceiverDesc(int id) {
		this.id = id;
		this.deleted = true;
	}

	public final int getId() {
		return this.id;
	}

	public final void setId(int id) {
		this.id = id;
	}

	public final boolean isDeleted() {
		return this.deleted;
	}

	public final String getRemoteURL() {
		return this.remoteURL;
	}

	public final void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

}
