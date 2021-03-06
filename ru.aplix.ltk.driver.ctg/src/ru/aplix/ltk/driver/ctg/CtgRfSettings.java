package ru.aplix.ltk.driver.ctg;

import static ru.aplix.ltk.core.util.IntSet.FULL_INT_SET;
import static ru.aplix.ltk.core.util.IntSet.INT_SET_PARAMATER_TYPE;
import static ru.aplix.ltk.core.util.NumericParameterType.INTEGER_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.NumericParameterType.LONG_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.BOOLEAN_PARAMETER_TYPE;
import static ru.aplix.ltk.core.util.ParameterType.STRING_PARAMETER_TYPE;
import ru.aplix.ltk.core.AbstractRfSettings;
import ru.aplix.ltk.core.util.IntSet;
import ru.aplix.ltk.core.util.Parameter;
import ru.aplix.ltk.core.util.Parameters;


/**
 * RFID settings for contiguous RFID driver.
 *
 * <p>At least a {@link #setReaderHost(String) reader host} should be set in
 * order to connect to the reader. Other settings have reasonable defaults.</p>
 */
public class CtgRfSettings extends AbstractRfSettings {

	/**
	 * Reader device host parameter.
	 *
	 * @see #getReaderPort()
	 */
	public static final Parameter<String> CTG_RF_READER_HOST =
			STRING_PARAMETER_TYPE.parameter("readerHost");

	/**
	 * Reader device port parameter (5084 by default).
	 *
	 * @see #getReaderPort()
	 */
	public static final Parameter<Integer> CTG_RF_READER_PORT =
			INTEGER_PARAMETER_TYPE.parameter("readerPort").byDefault(5084);

	/**
	 * ROSpec identifier parameter.
	 *
	 * @see #getROSpecId()
	 */
	public static final Parameter<Integer> CTG_RF_ROSPEC_ID =
			INTEGER_PARAMETER_TYPE.parameter("roSpecId").byDefault(123);

	/**
	 * Exclusive usage mode parameter. False by default.
	 *
	 * @see #isExclusiveMode
	 */
	public static final Parameter<Boolean> CTG_RF_EXCLUSIVE_MODE =
			BOOLEAN_PARAMETER_TYPE.parameter("exclusiveMode")
			.byDefault(false);

	/**
	 * Antennas parameter. All antennas by default.
	 *
	 * @see #getAntennas()
	 */
	public static final Parameter<IntSet> CTG_RF_ANTENNAS =
			INT_SET_PARAMATER_TYPE.parameter("antennas")
			.byDefault(FULL_INT_SET);

	/**
	 * Reader connection timeout parameter.
	 *
	 * @see #getConnectionTimeout()
	 */
	public static final Parameter<Long> CTG_RF_CONNECTION_TIMEOUT =
			LONG_PARAMETER_TYPE.parameter("connectionTimeout")
			.byDefault(10000L);

	/**
	 * Reader transaction timeout parameter.
	 *
	 * @see #getTransactionTimeout()
	 */
	public static final Parameter<Long> CTG_RF_TRANSACTION_TIMEOUT =
			LONG_PARAMETER_TYPE.parameter("transactionTimeout")
			.byDefault(10000L);

	/**
	 * Delay between reconnection attempts parameter.
	 *
	 * @see #getReconnectionDelay()
	 */
	public static final Parameter<Long> CTG_RF_RECONNECTION_DELAY =
			LONG_PARAMETER_TYPE.parameter("reconnectionDelay")
			.byDefault(10000L);

	/**
	 * A period between keep-alive requests issued by reader device parameter.
	 *
	 * @see #getKeepAliveRequestPeriod()
	 */
	public static final
	Parameter<Integer> CTG_RF_KEEP_ALIVE_REQUEST_PERIOD =
			INTEGER_PARAMETER_TYPE.parameter("keepAliveRequestPeriod")
			.byDefault(10000);

	private String readerHost;
	private int readerPort = CTG_RF_READER_PORT.getDefault();
	private int roSpecId = CTG_RF_ROSPEC_ID.getDefault();
	private boolean exclusiveMode = CTG_RF_EXCLUSIVE_MODE.getDefault();
	private IntSet antennas = CTG_RF_ANTENNAS.getDefault();
	private long connectionTimeout = CTG_RF_CONNECTION_TIMEOUT.getDefault();
	private long transactionTimeout = CTG_RF_TRANSACTION_TIMEOUT.getDefault();
	private long reconnectionDelay = CTG_RF_RECONNECTION_DELAY.getDefault();
	private int keepAliveRequestPeriod =
			CTG_RF_KEEP_ALIVE_REQUEST_PERIOD.getDefault();

	@Override
	public String getTargetId() {

		final String address = getReaderAddress();
		final IntSet antennas = getAntennas();

		if (antennas.isFull()) {
			return address;
		}

		return antennas.toString() + '@' + address;
	}

	/**
	 * Returns the reader address, including its port.
	 *
	 * @return reader address.
	 */
	public final String getReaderAddress() {

		final String readerHost = getReaderHost();

		if (readerHost == null) {
			return "unknown";
		}

		final int readerPort = getReaderPort();

		if (readerPort == CTG_RF_READER_PORT.getDefault().intValue()) {
			return readerHost;
		}

		return readerHost + ':' + readerPort;
	}

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
	 * the default one.
	 */
	public void setReaderPort(int port) {
		this.readerPort = port > 0 ? port : CTG_RF_READER_PORT.getDefault();
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
	 * Whether a connection to the reader device should be established in
	 * exclusive mode.
	 *
	 * @return <code>true</code> for exclusive mode, or <code>false</code> for
	 * non-exclusive one.
	 */
	public boolean isExclusiveMode() {
		return this.exclusiveMode;
	}

	/**
	 * Changes the exclusive connection mode.
	 *
	 * <p>In exclusive mode the driver deletes all of RO specs from device,
	 * and another reader device clients stop receiving updates from it.
	 * Note that they won't be disconnected.</p>
	 *
	 * <p>In non-exclusive mode (the default) the driver only attempts to delete
	 * a RO spec with the {@link #getROSpecId() same ID} it is going to create.
	 * It is not an error if such RO spec does not exist.</p>
	 *
	 * @param exclusiveMode <code>true</code> for exclusive mode, or
	 * <code>false</code> for non-exclusive one.
	 */
	public void setExclusiveMode(boolean exclusiveMode) {
		this.exclusiveMode = exclusiveMode;
	}

	/**
	 * A set of antennas to read RFID tags from.
	 *
	 * @return a set of integer antenna identifiers.
	 */
	public IntSet getAntennas() {
		return this.antennas;
	}

	/**
	 * Sets antennas to read RFID tags from.
	 *
	 * @param antennas new set of integer antenna identifiers,
	 * or <code>null</code> to read all of them.
	 */
	public void setAntennas(IntSet antennas) {
		this.antennas =
				antennas != null ? antennas : CTG_RF_ANTENNAS.getDefault();
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
	 * to the default one.
	 */
	public void setConnectionTimeout(long timeout) {
		this.connectionTimeout =
				timeout < 0 ? CTG_RF_CONNECTION_TIMEOUT.getDefault() : timeout;
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
	 * negative value to set it to the default one.
	 */
	public void setTransactionTimeout(long timeout) {
		this.transactionTimeout =
				timeout < 0 ? CTG_RF_TRANSACTION_TIMEOUT.getDefault() : timeout;
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
	 * or negative value to set it to the default one
	 */
	public void setReconnectionDelay(long delay) {
		this.reconnectionDelay =
				delay < 0 ? CTG_RF_RECONNECTION_DELAY.getDefault() : delay;
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
	 * the default one.
	 */
	public void setKeepAliveRequestPeriod(int period) {
		this.keepAliveRequestPeriod =
				period < 0 ? CTG_RF_KEEP_ALIVE_REQUEST_PERIOD.getDefault()
				: period;
	}

	@Override
	protected void readSettings(Parameters params) {
		setReaderHost(params.valueOf(
				CTG_RF_READER_HOST,
				getReaderHost()));
		setReaderPort(params.valueOf(
				CTG_RF_READER_PORT,
				getReaderPort()));
		setROSpecId(params.valueOf(
				CTG_RF_ROSPEC_ID,
				getROSpecId()));
		setExclusiveMode(params.valueOf(
				CTG_RF_EXCLUSIVE_MODE,
				isExclusiveMode()));
		setAntennas(params.valueOf(
				CTG_RF_ANTENNAS,
				getAntennas()));
		setConnectionTimeout(params.valueOf(
				CTG_RF_CONNECTION_TIMEOUT,
				getConnectionTimeout()));
		setTransactionTimeout(params.valueOf(
				CTG_RF_TRANSACTION_TIMEOUT,
				getTransactionTimeout()));
		setReconnectionDelay(params.valueOf(
				CTG_RF_RECONNECTION_DELAY,
				getReconnectionDelay()));
		setKeepAliveRequestPeriod(params.valueOf(
				CTG_RF_KEEP_ALIVE_REQUEST_PERIOD,
				getKeepAliveRequestPeriod()));
	}

	@Override
	protected void writeSettings(Parameters params) {
		params.set(CTG_RF_READER_HOST, getReaderHost())
		.set(CTG_RF_READER_PORT, getReaderPort())
		.set(CTG_RF_ROSPEC_ID, getROSpecId())
		.set(CTG_RF_EXCLUSIVE_MODE, isExclusiveMode())
		.set(CTG_RF_ANTENNAS, getAntennas())
		.set(CTG_RF_CONNECTION_TIMEOUT, getConnectionTimeout())
		.set(CTG_RF_TRANSACTION_TIMEOUT, getTransactionTimeout())
		.set(CTG_RF_RECONNECTION_DELAY, getReconnectionDelay())
		.set(CTG_RF_KEEP_ALIVE_REQUEST_PERIOD, getKeepAliveRequestPeriod());
	}

	@Override
	public CtgRfSettings clone() {
		return (CtgRfSettings) super.clone();
	}

}
