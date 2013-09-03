package ru.aplix.ltk.driver.ctg.impl;

import static ru.aplix.ltk.driver.ctg.CtgRfConnector.*;


public final class CtgRfConfig implements Cloneable {

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
	public CtgRfConfig clone() {
		try {
			return (CtgRfConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

}
