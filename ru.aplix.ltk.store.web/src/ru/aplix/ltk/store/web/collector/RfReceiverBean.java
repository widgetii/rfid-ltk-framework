package ru.aplix.ltk.store.web.collector;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfReceiverEditor;


public class RfReceiverBean implements Comparable<RfReceiverBean> {

	private static final String INACTIVE_STATUS = "inactive";
	private static final String ACTIVE_STATUS = "active";
	private static final String READY_STATUS = "ready";
	private static final String ERROR_STATUS = "error";

	private int id;
	private String remoteURL;
	private String status = INACTIVE_STATUS;
	private String error;
	private String cause;
	private boolean active;

	public RfReceiverBean() {
	}

	public RfReceiverBean(RfReceiver<HttpRfSettings> receiver) {

		final RfReceiverEditor<HttpRfSettings> editor = receiver.modify();

		setId(receiver.getId());
		setRemoteURL(editor.getRfSettings().getCollectorURL().toString());
		update(receiver);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRemoteURL() {
		return this.remoteURL;
	}

	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
		this.status = active ? ACTIVE_STATUS : INACTIVE_STATUS;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getCause() {
		return this.cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public void edit(
			RfReceiverEditor<HttpRfSettings> editor)
	throws MalformedURLException {

		final HttpRfSettings settings = editor.getRfSettings();

		settings.setCollectorURL(new URL(getRemoteURL()));
		editor.setActive(isActive());
	}

	public RfReceiverBean update(RfReceiver<HttpRfSettings> receiver) {
		setActive(receiver.isActive());
		updateStatus(receiver);
		return this;
	}

	@Override
	public int compareTo(RfReceiverBean o) {
		return getId() - o.getId();
	}

	private void updateStatus(RfReceiver<HttpRfSettings> receiver) {
		this.error = null;
		this.cause = null;
		if (!isActive()) {
			this.status = INACTIVE_STATUS;
			return;
		}
		this.status = ACTIVE_STATUS;

		final RfStatusMessage lastStatus = receiver.getLastStatus();

		if (lastStatus == null) {
			return;
		}

		switch (lastStatus.getRfStatus()) {
		case RF_READY:
			this.status = READY_STATUS;
			return;
		case RF_ERROR:
			this.status = ERROR_STATUS;
			updateError(lastStatus);
			return;
		}
	}

	private void updateError(RfStatusMessage lastStatus) {

		final String errorMessage = lastStatus.getErrorMessage();

		if (errorMessage != null) {
			this.error = errorMessage;
		}

		final Throwable cause = lastStatus.getCause();

		if (cause != null) {

			final StringWriter trace = new StringWriter();

			try (PrintWriter out = new PrintWriter(trace)) {
				cause.printStackTrace(out);
			}

			this.cause = trace.toString();
		}
	}

}
