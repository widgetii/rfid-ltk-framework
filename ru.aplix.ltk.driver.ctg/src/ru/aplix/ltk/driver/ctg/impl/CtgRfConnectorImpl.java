package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.driver.ctg.CtgRfConnector;


public class CtgRfConnectorImpl implements CtgRfConnector {

	private final CtgRfConfig config = new CtgRfConfig();

	@Override
	public String getReaderHost() {
		return this.config.getReaderHost();
	}

	@Override
	public void setReaderHost(String host) {
		this.config.setReaderHost(host);
	}

	@Override
	public int getReaderPort() {
		return this.config.getReaderPort();
	}

	@Override
	public void setReaderPort(int port) {
		this.config.setReaderPort(port);
	}

	@Override
	public int getROSpecId() {
		return this.config.getROSpecId();
	}

	@Override
	public void setROSpecId(int roSpecId) {
		this.config.setROSpecId(roSpecId);
	}

	@Override
	public long getTransactionTimeout() {
		return this.config.getTransactionTimeout();
	}

	@Override
	public void setTransactionTimeout(long timeout) {
		this.config.setTransactionTimeout(timeout);
	}

	@Override
	public long getReconnectionDelay() {
		return this.config.getReconnectionDelay();
	}

	@Override
	public void setReconnectionDelay(long delay) {
		this.config.setReconnectionDelay(delay);
	}

	@Override
	public RfConnection connect() {
		return new CtgRfConnection(this.config.clone());
	}

}
