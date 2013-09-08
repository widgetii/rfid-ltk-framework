package ru.aplix.ltk.collector.http;

import static ru.aplix.ltk.core.source.RfStatus.RF_ERROR;
import ru.aplix.ltk.core.source.RfStatus;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class RfStatusRequest implements RfStatusMessage, Parameterized {

	private String rfReaderId;
	private RfStatus rfStatus = RF_ERROR;
	private String errorMessage;

	public RfStatusRequest(RfStatusMessage status) {
		this.rfReaderId = status.getRfReaderId();
		this.rfStatus = status.getRfStatus();
		this.errorMessage = status.getErrorMessage();
	}

	public RfStatusRequest(Parameters parameters) {
		read(parameters);
	}

	@Override
	public String getRfReaderId() {
		return this.rfReaderId;
	}

	@Override
	public RfStatus getRfStatus() {
		return this.rfStatus;
	}

	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public Throwable getCause() {
		return null;
	}

	@Override
	public void read(Parameters params) {
		this.rfReaderId =
				params.valueOf("rfReaderId", null, getRfReaderId());
		this.rfStatus = params.enumValueOf(
				RfStatus.class,
				"rfStatus",
				RF_ERROR,
				getRfStatus());
		this.errorMessage =
				params.valueOf("errorMessage", null, getErrorMessage());
	}

	@Override
	public void write(Parameters params) {
		params.set("rfReaderId", getRfReaderId())
		.set("rfStatus", getRfStatus())
		.set("errorMessage", getErrorMessage());
	}

}
