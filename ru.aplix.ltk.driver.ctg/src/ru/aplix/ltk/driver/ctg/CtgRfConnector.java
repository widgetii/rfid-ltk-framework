package ru.aplix.ltk.driver.ctg;

import ru.aplix.ltk.core.RfConnector;


public interface CtgRfConnector extends RfConnector {

	int CTG_RF_DEFAULT_READER_PORT = 5084;
	int CTG_RF_DEFAULT_ROSPEC_ID = 123;
	long CTG_RF_DEFAULT_TRANSACTION_TIMEOUT = 10000L;
	long CTG_RF_DEFAULT_RECONNECTION_DELAY = 10000L;
	int CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD = 10000;

	String getReaderHost();

	void setReaderHost(String host);

	int getReaderPort();

	void setReaderPort(int port);

	int getROSpecId();

	void setROSpecId(int roSpecId);

	long getTransactionTimeout();

	void setTransactionTimeout(long timeout);

	long getReconnectionDelay();

	void setReconnectionDelay(long delay);

	int getKeepAliveRequestPeriod();

	void setKeepAliveRequestPeriod(int period);

}
