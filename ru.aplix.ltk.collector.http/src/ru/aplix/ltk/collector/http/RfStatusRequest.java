package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.collector.http.RemoteClrException.REMOTE_CLR_EXCEPTION_PARAMETER_TYPE;
import static ru.aplix.ltk.core.source.RfStatus.RF_ERROR;
import static ru.aplix.ltk.core.util.ParameterType.STRING_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.UUID_PARAMETER_TYPE;

import java.util.UUID;

import ru.aplix.ltk.core.source.RfStatus;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.util.*;


/**
 * An HTTP request containing RFID status update.
 *
 * <p>This request is sent by HTTP collector to its client to inform about
 * status updates.</p>
 *
 * <p>This request is sent by HTTP PUT to
 * {@link ClrClientRequest#getClientURL() client's URL}.</p>
 */
public class RfStatusRequest implements RfStatusMessage, Parameterized {

	static final Parameter<UUID> CLIENT =
			UUID_PARAMETER_TYPE.parameter("client");
	static final Parameter<String> TYPE =
			STRING_PARAMETER_TYPE.parameter("type");
	private static final Parameter<String> RF_READER_ID =
			STRING_PARAMETER_TYPE.parameter("readerId");
	private static final Parameter<RfStatus> RF_STATUS =
			new EnumParameterType<>(RfStatus.class)
			.parameter("rfStatus")
			.byDefault(RF_ERROR);
	private static final Parameter<String> ERROR_MESSAGE =
			STRING_PARAMETER_TYPE.parameter("errorMessage");
	private static final Parameter<Throwable> CAUSE =
			REMOTE_CLR_EXCEPTION_PARAMETER_TYPE.parameter("cause");

	private ClrClientId clientId;
	private String rfReaderId;
	private RfStatus rfStatus = RF_STATUS.getDefault();
	private String errorMessage;
	private Throwable cause;

	/**
	 * Constructs an empty request.
	 */
	public RfStatusRequest() {
	}

	/**
	 * Constructs a request from the given status update message.
	 *
	 * @param clientId a client identifier to send the request to.
	 * @param status an RFID status update to send.
	 */
	public RfStatusRequest(ClrClientId clientId, RfStatusMessage status) {
		this.clientId = clientId;
		this.rfReaderId = status.getRfReaderId();
		this.rfStatus = status.getRfStatus();
		this.errorMessage = status.getErrorMessage();
		this.cause = status.getCause();
	}

	/**
	 * Reconstruct a request from the given parameters.
	 *
	 * @param parameters parameters containing the constructed request's data.
	 */
	public RfStatusRequest(Parameters parameters) {
		read(parameters);
	}

	@Override
	public String getRfReaderId() {
		return this.rfReaderId;
	}

	public void setRfReaderId(String rfReaderId) {
		this.rfReaderId = rfReaderId;
	}

	@Override
	public RfStatus getRfStatus() {
		return this.rfStatus;
	}

	public void setRfStatus(RfStatus rfStatus) {
		this.rfStatus = rfStatus;
	}

	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * A cause of this error.
	 *
	 * <p>This method can return an instance of {@link RemoteClrException}
	 * when reconstructed from parameters, e.g. at HTTP collector's client
	 * side.</p>
	 *
	 * @return a throwable caused this error, or <code>null</code> if nothing
	 * to report.
	 */
	@Override
	public Throwable getCause() {
		return this.cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public void read(Parameters params) {
		setRfReaderId(params.valueOf(RF_READER_ID, getRfReaderId()));
		setRfStatus(params.valueOf(RF_STATUS, getRfStatus()));
		setErrorMessage(params.valueOf(ERROR_MESSAGE, getErrorMessage()));
		setCause(params.valueOf(CAUSE, getCause()));
	}

	@Override
	public void write(Parameters params) {
		if (this.clientId != null) {
			params.set(CLIENT, this.clientId.getUUID());
		}
		params.set(TYPE, "status")
		.set(RF_READER_ID, getRfReaderId())
		.set(RF_STATUS, getRfStatus())
		.set(ERROR_MESSAGE, getErrorMessage())
		.set(CAUSE, getCause());
	}

}
