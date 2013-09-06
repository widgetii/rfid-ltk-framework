package ru.aplix.ltk.driver.ctg;

import ru.aplix.ltk.core.AbstractRfSettings;
import ru.aplix.ltk.core.util.HttpParams;


public class CtgRfSettings extends AbstractRfSettings {

	public static final int CTG_RF_DEFAULT_READER_PORT = 5084;
	public static final int CTG_RF_DEFAULT_ROSPEC_ID = 123;
	public static final long CTG_RF_DEFAULT_CONNECTION_TIMEOUT = 10000L;
	public static final long CTG_RF_DEFAULT_TRANSACTION_TIMEOUT = 10000L;
	public static final long CTG_RF_DEFAULT_RECONNECTION_DELAY = 10000L;
	public static final int CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD = 10000;

	private String readerHost;
	private int readerPort = CTG_RF_DEFAULT_READER_PORT;
	private int roSpecId = CTG_RF_DEFAULT_ROSPEC_ID;
	private long connectionTimeout = CTG_RF_DEFAULT_CONNECTION_TIMEOUT;
	private long transactionTimeout = CTG_RF_DEFAULT_TRANSACTION_TIMEOUT;
	private long reconnectionDelay = CTG_RF_DEFAULT_RECONNECTION_DELAY;
	private int keepAliveRequestPeriod =
			CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD;

	public String getReaderHost() {
		return this.readerHost;
	}

	public void setReaderHost(String host) {
		this.readerHost = host;
	}

	public int getReaderPort() {
		return this.readerPort;
	}

	public void setReaderPort(int port) {
		this.readerPort = port > 0 ? port : CTG_RF_DEFAULT_READER_PORT;
	}

	public int getROSpecId() {
		return this.roSpecId;
	}

	public void setROSpecId(int roSpecId) {
		this.roSpecId = roSpecId;
	}

	public long getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(long timeout) {
		this.connectionTimeout =
				timeout < 0 ? CTG_RF_DEFAULT_CONNECTION_TIMEOUT : timeout;
	}

	public long getTransactionTimeout() {
		return this.transactionTimeout;
	}

	public void setTransactionTimeout(long timeout) {
		this.transactionTimeout = timeout;
	}

	public long getReconnectionDelay() {
		return this.reconnectionDelay;
	}

	public void setReconnectionDelay(long delay) {
		this.reconnectionDelay =
				delay <= 0 ? CTG_RF_DEFAULT_RECONNECTION_DELAY : delay;
	}

	public int getKeepAliveRequestPeriod() {
		return this.keepAliveRequestPeriod;
	}

	public void setKeepAliveRequestPeriod(int period) {
		this.keepAliveRequestPeriod =
				period < 0 || period > Integer.MAX_VALUE
				? CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD : period;
	}

	@Override
	protected void httpDecodeSettings(HttpParams params) {
		setReaderHost(params.valueOf(
				"readerHost",
				null,
				getReaderHost()));
		setReaderPort(params.intValueOf(
				"readerPort",
				CTG_RF_DEFAULT_READER_PORT,
				getReaderPort()));
		setROSpecId(params.intValueOf(
				"roSpecId",
				CTG_RF_DEFAULT_ROSPEC_ID,
				getROSpecId()));
		setConnectionTimeout(params.longValueOf(
				"connectionTimeout",
				CTG_RF_DEFAULT_CONNECTION_TIMEOUT,
				getConnectionTimeout()));
		setTransactionTimeout(params.longValueOf(
				"transactionTimeout",
				CTG_RF_DEFAULT_TRANSACTION_TIMEOUT,
				getTransactionTimeout()));
		setReconnectionDelay(params.longValueOf(
				"reconnectionDelay",
				CTG_RF_DEFAULT_RECONNECTION_DELAY,
				getReconnectionDelay()));
		setKeepAliveRequestPeriod(params.intValueOf(
				"keepAliveRequestPeriod",
				CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD,
				getKeepAliveRequestPeriod()));
	}

	@Override
	protected void httpEncodeSettings(HttpParams params) {
		params.set("readerHost", getReaderHost())
		.set("readerPort", getReaderPort())
		.set("roSpecId", getROSpecId())
		.set("connectionTimeout", getConnectionTimeout())
		.set("transactionTimeout", getTransactionTimeout())
		.set("reconnectionDelay", getReconnectionDelay())
		.set("keepAliveRequestPeriod", getKeepAliveRequestPeriod());
	}

	@Override
	public CtgRfSettings clone() {
		return (CtgRfSettings) super.clone();
	}

}
