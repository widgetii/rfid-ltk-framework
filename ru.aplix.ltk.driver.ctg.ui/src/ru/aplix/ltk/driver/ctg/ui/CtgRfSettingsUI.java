package ru.aplix.ltk.driver.ctg.ui;

import static ru.aplix.ltk.driver.ctg.CtgRfConnector.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import ru.aplix.ltk.driver.ctg.CtgRfConnector;


public class CtgRfSettingsUI extends JPanel {

	private static final long serialVersionUID = -4493305190228105924L;

	private static final String[] READER_PORTS = {
		Integer.toString(CTG_RF_DEFAULT_READER_PORT)
	};
	private static final String[] RO_SPEC_IDS = {
		Integer.toString(CTG_RF_DEFAULT_ROSPEC_ID)
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

	private final CtgRfConnectorUI connectorUI;

	private final JTextField readerHost;
	private final JComboBox<String> readerPort;
	private final JComboBox<String> roSpecId;
	private final JComboBox<String> transactionTimeout;
	private final JComboBox<String> reconnectionDelay;
	private final JComboBox<String> keepAliveRequestPeriod;

	private int line;

	public CtgRfSettingsUI(CtgRfConnectorUI connectorUI) {
		super(new GridBagLayout());
		this.connectorUI = connectorUI;

		this.readerHost = new JTextField(15);
		this.readerPort = new JComboBox<>(READER_PORTS);
		this.readerPort.setEditable(true);
		this.roSpecId = new JComboBox<>(RO_SPEC_IDS);
		this.roSpecId.setEditable(true);
		this.transactionTimeout = new JComboBox<>(TRANSACTION_TIMEOUTS);
		this.transactionTimeout.setEditable(true);
		this.reconnectionDelay = new JComboBox<>(RECONNECTION_DELAYS);
		this.reconnectionDelay.setEditable(true);
		this.keepAliveRequestPeriod =
				new JComboBox<>(KEEP_ALIVE_REQUEST_PERIODS);
		this.keepAliveRequestPeriod.setEditable(true);

		addOption("Адрес ридера", this.readerHost);
		addOption("Порт ридера", this.readerPort);
		addOption("Идентификатор клиента (число)", this.roSpecId);
		addOption("Время ожидания ответа, мс", this.transactionTimeout);
		addOption("Задержка переподключения, мс", this.reconnectionDelay);
		addOption(
				"Периодичность запросов поддержки соединения, мс",
				this.keepAliveRequestPeriod);

		applyConnector();
	}

	public final CtgRfConnector getConnector() {
		return this.connectorUI.getConnector();
	}

	public void configureConnector() {

		final CtgRfConnector connector = getConnector();

		connector.setReaderHost(this.readerHost.getText());
		connector.setReaderPort(intOption(
				this.readerPort,
				CTG_RF_DEFAULT_READER_PORT));
		connector.setROSpecId(intOption(
				this.roSpecId,
				CTG_RF_DEFAULT_ROSPEC_ID));
		connector.setTransactionTimeout(longOption(
				this.transactionTimeout,
				CTG_RF_DEFAULT_TRANSACTION_TIMEOUT));
		connector.setReconnectionDelay(longOption(
				this.reconnectionDelay,
				CTG_RF_DEFAULT_RECONNECTION_DELAY));
		connector.setKeepAliveRequestPeriod(intOption(
				this.keepAliveRequestPeriod,
				CTG_RF_DEFAULT_KEEP_ALIVE_REQUEST_PERIOD));
	}

	private void applyConnector() {

		final CtgRfConnector connector = getConnector();

		this.readerHost.setText(optionValue(connector.getReaderHost()));
		this.readerPort.setSelectedItem(
				Integer.toString(connector.getReaderPort()));
		this.roSpecId.setSelectedItem(
				Integer.toString(connector.getROSpecId()));
		this.transactionTimeout.setSelectedItem(
				Long.toString(connector.getTransactionTimeout()));
		this.reconnectionDelay.setSelectedItem(
				Long.toString(connector.getReconnectionDelay()));
		this.keepAliveRequestPeriod.setSelectedItem(
				Integer.toString(connector.getKeepAliveRequestPeriod()));
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
