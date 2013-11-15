package ru.aplix.ltk.store.web.receiver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import ru.aplix.ltk.collector.http.ClrAddress;
import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.RemoteClrException;
import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfReceiverEditor;


public class RfReceiverBean
		extends AbstractRfReceiverBean
		implements Comparable<RfReceiverBean> {

	private static final String INACTIVE_STATUS = "inactive";
	private static final String ACTIVE_STATUS = "active";
	private static final String READY_STATUS = "ready";
	private static final String ERROR_STATUS = "error";

	private String status = INACTIVE_STATUS;
	private String error;
	private String cause;
	private String providerId;
	private String profileId;
	private boolean active;

	public RfReceiverBean() {
	}

	public RfReceiverBean(RfReceiver<HttpRfSettings> receiver) {
		super(receiver);
		update(receiver);

		final String remoteURL = getRemoteURL();

		if (remoteURL != null) {

			final ClrAddress address = new ClrAddress(remoteURL);
			final ClrProfileId profileId = address.getProfileId();

			if (profileId != null) {
				this.providerId = profileId.getProviderId();
				this.profileId = profileId.getId();
			}
		}
	}

	public final String getProviderId() {
		return this.providerId;
	}

	public final void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public final String getProfileId() {
		return this.profileId;
	}

	public final void setProfileId(String profileId) {
		this.profileId = profileId;
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

	public RfReceiverBean update(RfReceiver<?> receiver) {
		setActive(receiver.isActive());
		updateStatus(receiver);
		return this;
	}

	@Override
	public int compareTo(RfReceiverBean o) {
		return getId() - o.getId();
	}

	private void updateStatus(RfReceiver<?> receiver) {
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

		if (cause == null) {
			return;
		}

		if (cause instanceof RemoteClrException) {
			updateRemoteCause((RemoteClrException) cause);
		} else {
			updateCause(cause);
		}
	}

	private void updateCause(Throwable cause) {

		final StringWriter trace = new StringWriter();

		try (PrintWriter out = new PrintWriter(trace)) {
			cause.printStackTrace(out);
		}

		this.cause = trace.toString();
	}

	private void updateRemoteCause(RemoteClrException cause) {
		this.cause = cause.getRemoteStackTrace();
	}

}
