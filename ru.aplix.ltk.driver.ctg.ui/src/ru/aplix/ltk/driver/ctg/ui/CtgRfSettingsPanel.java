package ru.aplix.ltk.driver.ctg.ui;

import static ru.aplix.ltk.core.collector.DefaultRfTracker.DEFAULT_INVALIDATION_TIMEOUT;
import static ru.aplix.ltk.core.collector.DefaultRfTracker.DEFAULT_TRANSACTION_TIMEOUT;
import static ru.aplix.ltk.driver.ctg.CtgRfSettings.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import ru.aplix.ltk.driver.ctg.CtgRfSettings;


public class CtgRfSettingsPanel extends JPanel {

	private static final long serialVersionUID = -4493305190228105924L;

	private static final String[] READER_PORTS = {
		Integer.toString(CTG_RF_DEFAULT_READER_PORT)
	};
	private static final String[] RO_SPEC_IDS = {
		Integer.toString(CTG_RF_DEFAULT_ROSPEC_ID)
	};
	private static final String[] CONNECTION_TIMEOUTS = {
		Long.toString(CTG_RF_DEFAULT_CONNECTION_TIMEOUT / 2),
		Long.toString(CTG_RF_DEFAULT_CONNECTION_TIMEOUT),
		Long.toString(CTG_RF_DEFAULT_CONNECTION_TIMEOUT * 2)
	};
	private static final String[] TRANSACTION_TIMEOUTS = {
		Long.toString(CTG_RF_DEFAULT_TRANSACTION_TIMEOUT / 4),
		Long.toString(CTG_RF_DEFAULT_TRANSACTION_TIMEOUT / 2),
		Long.toString(CTG_RF_DEFAULT_TRANSACTION_TIMEOUT)
	};
	private static final String[] RECONNECTION_DELAYS = {
		Long.toString(CTG_RF_DEFAULT_RECONNECTION_DELAY / 4),
		Long.toString(CTG_RF_DEFAULT_RECONNECTION_DELAY / 2),
		Long.toString(CTG_RF_DEFAULT_RECONNECTION_DELAY)
	};
	private static final String[] KEEP_ALIVE_REQUEST_PERIODS = {
		Long.toString(CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD / 2),
		Long.toString(CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD),
		Long.toString(CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD * 2),
	};
	private static final String[] TRANSACTION_PERIODS = {
		Long.toString(DEFAULT_TRANSACTION_TIMEOUT / 2),
		Long.toString(DEFAULT_TRANSACTION_TIMEOUT),
		Long.toString(DEFAULT_TRANSACTION_TIMEOUT * 2),
	};
	private static final String[] INVALIDATION_PERIODS = {
		Long.toString(DEFAULT_INVALIDATION_TIMEOUT / 2),
		Long.toString(DEFAULT_INVALIDATION_TIMEOUT),
		Long.toString(DEFAULT_INVALIDATION_TIMEOUT * 2),
	};

	private final CtgRfSettingsUI settingsUI;

	private final JComboBox<String> readerHost;
	private final JComboBox<String> readerPort;
	private final JComboBox<String> roSpecId;
	private final JComboBox<String> connectionTimeout;
	private final JComboBox<String> transactionTimeout;
	private final JComboBox<String> reconnectionDelay;
	private final JComboBox<String> keepAliveRequestPeriod;
	private final JComboBox<String> tagTransactionTimeout;
	private final JComboBox<String> tagInvalidationTimeout;

	private int line;

	public CtgRfSettingsPanel(CtgRfSettingsUI settingsUI) {
		super(new GridBagLayout());
		this.settingsUI = settingsUI;

		this.readerHost = new JComboBox<>();
		this.readerHost.setEditable(true);
		this.readerPort = new JComboBox<>(READER_PORTS);
		this.readerPort.setEditable(true);
		this.roSpecId = new JComboBox<>(RO_SPEC_IDS);
		this.roSpecId.setEditable(true);
		this.connectionTimeout = new JComboBox<>(CONNECTION_TIMEOUTS);
		this.connectionTimeout.setEditable(true);
		this.transactionTimeout = new JComboBox<>(TRANSACTION_TIMEOUTS);
		this.transactionTimeout.setEditable(true);
		this.reconnectionDelay = new JComboBox<>(RECONNECTION_DELAYS);
		this.reconnectionDelay.setEditable(true);
		this.keepAliveRequestPeriod =
				new JComboBox<>(KEEP_ALIVE_REQUEST_PERIODS);
		this.keepAliveRequestPeriod.setEditable(true);
		this.tagTransactionTimeout = new JComboBox<>(TRANSACTION_PERIODS);
		this.tagTransactionTimeout.setEditable(true);
		this.tagInvalidationTimeout = new JComboBox<>(INVALIDATION_PERIODS);
		this.tagInvalidationTimeout.setEditable(true);

		addOption("Адрес ридера", this.readerHost);
		addOption("Порт ридера", this.readerPort);
		addOption("Идентификатор клиента (число)", this.roSpecId);
		addOption("Время ожидания соединения, мс", this.connectionTimeout);
		addOption("Время ожидания ответа, мс", this.transactionTimeout);
		addOption("Задержка переподключения, мс", this.reconnectionDelay);
		addOption(
				"Период запросов поддержки соединения, мс",
				this.keepAliveRequestPeriod);
		addOption(
				"Период проверки исчезновения тегов, мс",
				this.tagTransactionTimeout);
		addOption(
				"Задержка исчезновения тега, мс",
				this.tagInvalidationTimeout);

		applySettings();
	}

	public final CtgRfSettings getSettings() {
		return this.settingsUI.getSettings();
	}

	public void fillSettings() {

		final CtgRfSettings settings = getSettings();

		settings.setReaderHost((String) this.readerHost.getSelectedItem());
		settings.setReaderPort(intOption(
				this.readerPort,
				CTG_RF_DEFAULT_READER_PORT));
		settings.setROSpecId(intOption(
				this.roSpecId,
				CTG_RF_DEFAULT_ROSPEC_ID));
		settings.setConnectionTimeout(longOption(
				this.connectionTimeout,
				CTG_RF_DEFAULT_CONNECTION_TIMEOUT));
		settings.setTransactionTimeout(longOption(
				this.transactionTimeout,
				CTG_RF_DEFAULT_TRANSACTION_TIMEOUT));
		settings.setReconnectionDelay(longOption(
				this.reconnectionDelay,
				CTG_RF_DEFAULT_RECONNECTION_DELAY));
		settings.setKeepAliveRequestPeriod(intOption(
				this.keepAliveRequestPeriod,
				CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD));

		final CtgRfSettingsUI ui = this.settingsUI;

		ui.getTrackingPolicy().setTransactionTimeout(longOption(
				this.tagTransactionTimeout,
				DEFAULT_TRANSACTION_TIMEOUT));
		ui.getTrackingPolicy().setInvalidationTimeout(longOption(
				this.tagInvalidationTimeout,
				DEFAULT_INVALIDATION_TIMEOUT));
	}

	private void applySettings() {

		final CtgRfSettings settings = getSettings();

		this.readerHost.setSelectedItem(
				optionValue(settings.getReaderHost()));
		this.readerPort.setSelectedItem(
				Integer.toString(settings.getReaderPort()));
		this.roSpecId.setSelectedItem(
				Integer.toString(settings.getROSpecId()));
		this.connectionTimeout.setSelectedItem(
				Long.toString(settings.getConnectionTimeout()));
		this.transactionTimeout.setSelectedItem(
				Long.toString(settings.getTransactionTimeout()));
		this.reconnectionDelay.setSelectedItem(
				Long.toString(settings.getReconnectionDelay()));
		this.keepAliveRequestPeriod.setSelectedItem(
				Integer.toString(settings.getKeepAliveRequestPeriod()));

		final CtgRfSettingsUI ui = this.settingsUI;

		this.tagTransactionTimeout.setSelectedItem(
				Long.toString(ui.getTrackingPolicy().getTransactionTimeout()));
		this.tagInvalidationTimeout.setSelectedItem(
				Long.toString(ui.getTrackingPolicy().getInvalidationTimeout()));
	}

	private void addOption(String label, JComponent component) {

		final GridBagConstraints labelConstraints = new GridBagConstraints();

		labelConstraints.anchor = GridBagConstraints.EAST;
		labelConstraints.fill = GridBagConstraints.NONE;
		labelConstraints.gridx = 0;
		labelConstraints.gridy = this.line;
		labelConstraints.insets = new Insets(2, 10, 2, 2);

		add(new JLabel(label), labelConstraints);

		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridx = 1;
		constraints.gridy = this.line;
		constraints.weightx = 1;
		constraints.insets = new Insets(2, 2, 2, 10);

		add(component, constraints);

		++this.line;
	}

	private static String optionValue(Object value) {
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	private static int intOption(JComboBox<?> comboBox, int defaultValue) {

		final long value = longOption(comboBox, defaultValue);

		if (value > Integer.MAX_VALUE) {
			comboBox.setSelectedItem(Integer.toString(defaultValue));
			return defaultValue;
		}

		return (int) value;
	}

	private static long longOption(JComboBox<?> comboBox, long defaultValue) {

		final String selected = (String) comboBox.getSelectedItem();

		if (selected != null && !selected.isEmpty()) {
			try {
				return Long.parseLong(selected);
			} catch (NumberFormatException e) {
			}
		}

		comboBox.setSelectedItem(Long.toString(defaultValue));

		return defaultValue;
	}

}
