package ru.aplix.ltk.driver.ctg;

import ru.aplix.ltk.core.AbstractRfSettings;
import ru.aplix.ltk.core.util.Parameters;


/**
 * RFID settings for contiguous RFID driver.
 *
 * <p>At least a {@link #setReaderHost(String) reader host} should be set in
 * order to connect to the reader. Other settings have reasonable defaults.</p>
 */
public class CtgRfSettings extends AbstractRfSettings {

	/**
	 * Default reader device port number (5084).
	 *
	 * @see #getReaderPort()
	 */
	public static final int CTG_RF_DEFAULT_READER_PORT = 5084;

	/**
	 * Default ROSpec identifier to use.
	 *
	 * @see #getROSpecId()
	 */
	public static final int CTG_RF_DEFAULT_ROSPEC_ID = 123;

	/**
	 * Default reader connection timeout.
	 *
	 * @see #getConnectionTimeout()
	 */
	public static final long CTG_RF_DEFAULT_CONNECTION_TIMEOUT = 10000L;

	/**
	 * Default reader transaction timeout.
	 *
	 * @see #getTransactionTimeout()
	 */
	public static final long CTG_RF_DEFAULT_TRANSACTION_TIMEOUT = 10000L;

	/**
	 * Default delay between reconnection attempts.
	 *
	 * @see #getReconnectionDelay()
	 */
	public static final long CTG_RF_DEFAULT_RECONNECTION_DELAY = 10000L;

	/**
	 * Default period between keep-alive requests issued by reader device.
	 *
	 * @see #getKeepAliveRequestPeriod()
	 */
	public static final int CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD = 10000;

	private String readerHost;
	private int readerPort = CTG_RF_DEFAULT_READER_PORT;
	private int roSpecId = CTG_RF_DEFAULT_ROSPEC_ID;
	private long connectionTimeout = CTG_RF_DEFAULT_CONNECTION_TIMEOUT;
	private long transactionTimeout = CTG_RF_DEFAULT_TRANSACTION_TIMEOUT;
	private long reconnectionDelay = CTG_RF_DEFAULT_RECONNECTION_DELAY;
	private int keepAliveRequestPeriod =
			CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD;

	/**
	 * Reader device host.
	 *
	 * @return host name, IP address, or <code>null</code> if not set.
	 */
	public String getReaderHost() {
		return this.readerHost;
	}

	/**
	 * Sets the reader device host.
	 *
	 * <p>This option is mandatory to set before connection attempt.</p>
	 *
	 * @param host new reader host.
	 */
	public void setReaderHost(String host) {
		this.readerHost = host;
	}

	/**
	 * Reader device port.
	 *
	 * @return reader port number.
	 */
	public int getReaderPort() {
		return this.readerPort;
	}

	/**
	 * Sets the reader port.
	 *
	 * @param port new reader port number, or non-positive value to drop it to
	 * the {@link #CTG_RF_DEFAULT_READER_PORT default one}.
	 */
	public void setReaderPort(int port) {
		this.readerPort = port > 0 ? port : CTG_RF_DEFAULT_READER_PORT;
	}

	/**
	 * Reader ROSpec identifier.
	 *
	 * <p>See <a href="http://www.gs1.org/gsmp/kc/epcglobal/llrp/llrp_1_0_1-standard-20070813.pdf">
	 * LLRP specification</a>, section 10, specifically subsection 10.2.1.</p>
	 *
	 * @return ROSpec identifier.
	 */
	public int getROSpecId() {
		return this.roSpecId;
	}

	/**
	 * Sets the ROSpec identifier.
	 *
	 * <p>This identifier should be unique for each client connecting to the
	 * device. This driver opens at most one connection with the given
	 * host/port/ROSpecID triple. So, no need to specify an unique one, unless
	 * you know what you doing.</p>
	 *
	 * @param roSpecId new ROSpec identifier. Should be a positive number.
	 */
	public void setROSpecId(int roSpecId) {
		this.roSpecId = roSpecId;
	}

	/**
	 * Reader device connection timeout.
	 *
	 * @return timeout in milliseconds.
	 */
	public long getConnectionTimeout() {
		return this.connectionTimeout;
	}

	/**
	 * Sets reader device connection timeout.
	 *
	 * @param timeout new timeout in milliseconds, or negative value to set it
	 * to the {@link #CTG_RF_DEFAULT_CONNECTION_TIMEOUT default one}.
	 */
	public void setConnectionTimeout(long timeout) {
		this.connectionTimeout =
				timeout < 0 ? CTG_RF_DEFAULT_CONNECTION_TIMEOUT : timeout;
	}

	/**
	 * Reader device transaction timeout.
	 *
	 * <p>This is a time the driver waits for the reader response after request
	 * is sent.</p>
	 *
	 * @return timeout in milliseconds.
	 */
	public long getTransactionTimeout() {
		return this.transactionTimeout;
	}

	/**
	 * Sets the reader device transaction timeout.
	 *
	 * @param timeout new timeout in milliseconds, zero to wait indefinitely, or
	 * negative value to set it to the
	 * {@link #CTG_RF_DEFAULT_TRANSACTION_TIMEOUT default one}.
	 */
	public void setTransactionTimeout(long timeout) {
		this.transactionTimeout =
				timeout < 0 ? CTG_RF_DEFAULT_TRANSACTION_TIMEOUT : timeout;
	}

	/**
	 * The delay between reconnection attempts.
	 *
	 * @return delay in milliseconds.
	 *
	 * @see #getKeepAliveRequestPeriod()
	 */
	public long getReconnectionDelay() {
		return this.reconnectionDelay;
	}

	/**
	 * Sets the delay between reconnection attempts.
	 *
	 * @param delay new delay in milliseconds, zero to reconnect immediately,
	 * or negative value to set it to the
	 * {@link #CTG_RF_DEFAULT_RECONNECTION_DELAY default one}
	 */
	public void setReconnectionDelay(long delay) {
		this.reconnectionDelay =
				delay < 0 ? CTG_RF_DEFAULT_RECONNECTION_DELAY : delay;
	}

	/**
	 * The period between keep-alive requests issued by reader device.
	 *
	 * <p>The driver expects the reader device is sending keep-alive requests
	 * periodically. If no requests received for some time, then the driver
	 * considers the connection to the reader is lost, and starts attempts to
	 * {@link #getReconnectionDelay() reconnect}. The time driver waits for the
	 * reader requests before this happens is
	 * {@code keepAliveRequestPeriod * 3}.</p>
	 *
	 * <p>See <a href="http://www.gs1.org/gsmp/kc/epcglobal/llrp/llrp_1_0_1-standard-20070813.pdf">
	 * LLRP specification</a>, section 12, specifically subsection 12.2.4.</p>
	 *
	 * @return period in milliseconds.
	 */
	public int getKeepAliveRequestPeriod() {
		return this.keepAliveRequestPeriod;
	}

	/**
	 * Sets the period between keep-alive requests issues by reader device.
	 *
	 * @param period new period in milliseconds, or negative value to set it to
	 * the {@link #CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD default one}.
	 */
	public void setKeepAliveRequestPeriod(int period) {
		this.keepAliveRequestPeriod =
				period < 0 ? CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD : period;
	}

	@Override
	protected void readSettings(Parameters params) {
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
	protected void writeSettings(Parameters params) {
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
