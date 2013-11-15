package ru.aplix.ltk.store.web.receiver;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfReceiverEditor;


public abstract class AbstractRfReceiverBean {

	private int id;
	private String remoteURL;

	public AbstractRfReceiverBean() {
	}

	public AbstractRfReceiverBean(RfReceiver<HttpRfSettings> receiver) {
		this(receiver, true);
		updateRemoteURL(receiver);
	}

	protected AbstractRfReceiverBean(
			RfReceiver<?> receiver,
			@SuppressWarnings("unused") boolean http) {
		this.id = receiver.getId();
	}

	protected AbstractRfReceiverBean(int id) {
		this.id = id;
	}

	public final int getId() {
		return this.id;
	}

	public final void setId(int id) {
		this.id = id;
	}

	public final String getRemoteURL() {
		return this.remoteURL;
	}

	public final void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	public void updateRemoteURL(RfReceiver<HttpRfSettings> receiver) {

		final RfReceiverEditor<HttpRfSettings> editor = receiver.modify();

		this.remoteURL = editor.getRfSettings().getCollectorURL().toString();
	}

}
