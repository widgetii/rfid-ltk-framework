package ru.aplix.ltk.store.web.ctg;

import static ru.aplix.ltk.core.util.IntSet.parseIntSet;
import static ru.aplix.ltk.driver.ctg.CtgRfSettings.*;
import ru.aplix.ltk.collector.http.client.HttpRfProfile;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;
import ru.aplix.ltk.store.web.rcm.ui.RcmUISettings;


public class CtgRcmUISettings extends RcmUISettings<CtgRfSettings> {

	private String readerHost;
	private Integer readerPort;
	private Integer roSpecId;
	private Boolean exclusiveMode;
	private String antennas;
	private Long connectionTimeout;
	private Long transactionTimeout;
	private Long reconnectionDelay;
	private Integer keepAliveRequestPeriod;

	public CtgRcmUISettings() {
	}

	public CtgRcmUISettings(HttpRfProfile<CtgRfSettings> profile) {
		super(profile);

		final CtgRfSettings settings = profile.getSettings().getSettings();

		this.readerHost = settings.getReaderHost();
		this.readerPort = settings.getReaderPort();
		this.roSpecId = settings.getROSpecId();
		this.exclusiveMode = settings.isExclusiveMode();
		this.antennas = settings.getAntennas().toString();
		this.connectionTimeout = settings.getConnectionTimeout();
		this.transactionTimeout = settings.getTransactionTimeout();
		this.reconnectionDelay = settings.getReconnectionDelay();
		this.keepAliveRequestPeriod = settings.getKeepAliveRequestPeriod();
	}

	public String getReaderHost() {
		return this.readerHost;
	}

	public void setReaderHost(String readerHost) {
		this.readerHost = readerHost;
	}

	public Integer getReaderPort() {
		return this.readerPort;
	}

	public void setReaderPort(Integer readerPort) {
		this.readerPort = readerPort;
	}

	public Integer getRoSpecId() {
		return this.roSpecId;
	}

	public void setRoSpecId(Integer roSpecId) {
		this.roSpecId = roSpecId;
	}

	public Boolean getExclusiveMode() {
		return this.exclusiveMode;
	}

	public void setExclusiveMode(Boolean exclusiveMode) {
		this.exclusiveMode = exclusiveMode;
	}

	public String getAntennas() {
		return this.antennas;
	}

	public void setAntennas(String antennas) {
		this.antennas = antennas;
	}

	public Long getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(Long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Long getTransactionTimeout() {
		return this.transactionTimeout;
	}

	public void setTransactionTimeout(Long transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}

	public Long getReconnectionDelay() {
		return this.reconnectionDelay;
	}

	public void setReconnectionDelay(Long reconnectionDelay) {
		this.reconnectionDelay = reconnectionDelay;
	}

	public Integer getKeepAliveRequestPeriod() {
		return this.keepAliveRequestPeriod;
	}

	public void setKeepAliveRequestPeriod(Integer keepAliveRequestPeriod) {
		this.keepAliveRequestPeriod = keepAliveRequestPeriod;
	}

	@Override
	protected void fillSettings(CtgRfSettings settings) {
		settings.setReaderHost(getReaderHost());
		settings.setReaderPort(
				CTG_RF_READER_PORT.valueOrDefault(getReaderPort()));
		settings.setROSpecId(
				CTG_RF_ROSPEC_ID.valueOrDefault(getRoSpecId()));
		settings.setExclusiveMode(
				CTG_RF_EXCLUSIVE_MODE.valueOrDefault(getExclusiveMode()));

		final String antennas = getAntennas();

		if (antennas == null) {
			settings.setAntennas(CTG_RF_ANTENNAS.getDefault());
		} else {
			settings.setAntennas(parseIntSet(antennas));
		}

		settings.setConnectionTimeout(
				CTG_RF_CONNECTION_TIMEOUT.valueOrDefault(
						getConnectionTimeout()));
		settings.setTransactionTimeout(
				CTG_RF_TRANSACTION_TIMEOUT.valueOrDefault(
						getTransactionTimeout()));
		settings.setReconnectionDelay(
				CTG_RF_RECONNECTION_DELAY.valueOrDefault(
						getReconnectionDelay()));
		settings.setKeepAliveRequestPeriod(
				CTG_RF_KEEP_ALIVE_REQUEST_PERIOD.valueOrDefault(
						getKeepAliveRequestPeriod()));
	}

}
