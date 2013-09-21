package ru.aplix.ltk.driver.ctg.ui;

import static ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy.RF_INVALIDATION_TIMEOUT;
import static ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy.RF_TRANSACTION_TIMEOUT;
import static ru.aplix.ltk.core.util.IntSet.parseIntSet;
import static ru.aplix.ltk.driver.ctg.CtgRfSettings.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import ru.aplix.ltk.core.util.IntSet;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;


public class CtgRfSettingsPanel extends JPanel {

	private static final long serialVersionUID = -4493305190228105924L;

	private static final String[] READER_PORTS = {
		CTG_RF_READER_PORT.getDefault().toString()
	};
	private static final String[] RO_SPEC_IDS = {
		CTG_RF_ROSPEC_ID.getDefault().toString()
	};
	private static final String[] ANTENNAS = {
		CTG_RF_ANTENNAS.getDefault().toString()
	};
	private static final String[] CONNECTION_TIMEOUTS = {
		Long.toString(CTG_RF_CONNECTION_TIMEOUT.getDefault() / 2),
		CTG_RF_CONNECTION_TIMEOUT.getDefault().toString(),
		Long.toString(CTG_RF_CONNECTION_TIMEOUT.getDefault() * 2)
	};
	private static final String[] TRANSACTION_TIMEOUTS = {
		Long.toString(CTG_RF_TRANSACTION_TIMEOUT.getDefault() / 4),
		Long.toString(CTG_RF_TRANSACTION_TIMEOUT.getDefault() / 2),
		CTG_RF_TRANSACTION_TIMEOUT.getDefault().toString()
	};
	private static final String[] RECONNECTION_DELAYS = {
		Long.toString(CTG_RF_RECONNECTION_DELAY.getDefault() / 4),
		Long.toString(CTG_RF_RECONNECTION_DELAY.getDefault() / 2),
		CTG_RF_RECONNECTION_DELAY.getDefault().toString()
	};
	private static final String[] KEEP_ALIVE_REQUEST_PERIODS = {
		Long.toString(CTG_RF_KEEP_ALIVE_REQUEST_PERIOD.getDefault() / 2),
		CTG_RF_KEEP_ALIVE_REQUEST_PERIOD.getDefault().toString(),
		Long.toString(CTG_RF_KEEP_ALIVE_REQUEST_PERIOD.getDefault() * 2),
	};
	private static final String[] TRANSACTION_PERIODS = {
		Long.toString(RF_TRANSACTION_TIMEOUT.getDefault() / 2),
		RF_TRANSACTION_TIMEOUT.getDefault().toString(),
		Long.toString(RF_TRANSACTION_TIMEOUT.getDefault() * 2),
	};
	private static final String[] INVALIDATION_PERIODS = {
		Long.toString(RF_INVALIDATION_TIMEOUT.getDefault() / 2),
		RF_INVALIDATION_TIMEOUT.getDefault().toString(),
		Long.toString(RF_INVALIDATION_TIMEOUT.getDefault() * 2),
	};

	private final CtgRfSettingsUI settingsUI;

	private final JComboBox<String> readerHost;
	private final JComboBox<String> readerPort;
	private final JComboBox<String> roSpecId;
	private final JCheckBox exclusiveMode;
	private final JComboBox<String> antennas;
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
		this.exclusiveMode = new JCheckBox("Монопольно");
		this.antennas = new JComboBox<>(ANTENNAS);
		this.antennas.setEditable(true);
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
		addOption(
				"Идентификатор клиента (число)",
				this.roSpecId,
				this.exclusiveMode);
		addOption("Антенны", this.antennas);
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
				CTG_RF_READER_PORT.getDefault()));
		settings.setROSpecId(intOption(
				this.roSpecId,
				CTG_RF_ROSPEC_ID.getDefault()));
		settings.setExclusiveMode(this.exclusiveMode.isSelected());
		settings.setAntennas(intSetOption(
				this.antennas,
				settings.getAntennas()));
		settings.setConnectionTimeout(longOption(
				this.connectionTimeout,
				CTG_RF_CONNECTION_TIMEOUT.getDefault()));
		settings.setTransactionTimeout(longOption(
				this.transactionTimeout,
				CTG_RF_TRANSACTION_TIMEOUT.getDefault()));
		settings.setReconnectionDelay(longOption(
				this.reconnectionDelay,
				CTG_RF_RECONNECTION_DELAY.getDefault()));
		settings.setKeepAliveRequestPeriod(intOption(
				this.keepAliveRequestPeriod,
				CTG_RF_KEEP_ALIVE_REQUEST_PERIOD.getDefault()));

		final CtgRfSettingsUI ui = this.settingsUI;

		ui.getTrackingPolicy().setTransactionTimeout(longOption(
				this.tagTransactionTimeout,
				RF_TRANSACTION_TIMEOUT.getDefault()));
		ui.getTrackingPolicy().setInvalidationTimeout(longOption(
				this.tagInvalidationTimeout,
				RF_INVALIDATION_TIMEOUT.getDefault()));
	}

	private void applySettings() {

		final CtgRfSettings settings = getSettings();

		this.readerHost.setSelectedItem(
				optionValue(settings.getReaderHost()));
		this.readerPort.setSelectedItem(
				Integer.toString(settings.getReaderPort()));
		this.roSpecId.setSelectedItem(
				Integer.toString(settings.getROSpecId()));
		this.exclusiveMode.setSelected(
				settings.isExclusiveMode());
		this.antennas.setSelectedItem(
				settings.getAntennas().toString());
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
		addOption(label, component, null);
	}

	private void addOption(
			String label,
			JComponent component,
			JComponent extra) {

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
		constraints.insets = new Insets(2, 2, 2, extra != null ? 2 : 10);

		add(component, constraints);

		if (extra != null) {

			final GridBagConstraints extraConstraints =
					new GridBagConstraints();

			extraConstraints.anchor = GridBagConstraints.WEST;
			extraConstraints.fill = GridBagConstraints.NONE;
			extraConstraints.gridx = 2;
			extraConstraints.gridy = this.line;
			extraConstraints.weightx = 0.3;
			extraConstraints.insets = new Insets(2, 2, 2, 10);

			add(extra, extraConstraints);
		}

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

	private static IntSet intSetOption(
			JComboBox<?> comboBox,
			IntSet defaultValue) {

		final String selected = (String) comboBox.getSelectedItem();

		if (selected != null) {
			try {
				return parseIntSet(selected);
			} catch (IllegalArgumentException e) {
			}
		}

		comboBox.setSelectedItem(defaultValue.toString());

		return defaultValue;
	}

}
