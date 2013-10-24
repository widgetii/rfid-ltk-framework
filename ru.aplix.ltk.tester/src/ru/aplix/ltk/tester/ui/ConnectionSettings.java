package ru.aplix.ltk.tester.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;


class ConnectionSettings extends JPanel implements RfProviderItemListener {

	private static final long serialVersionUID = -8803090880315132382L;

	private static final String BLANK_CARD = "blank";

	private final ConnectionTab connectionTab;
	private final JPanel settingsPanel;
	private final ConnectionControls connectionControls;
	private final CardLayout cardLayout;

	ConnectionSettings(ConnectionTab connectionTab) {
		super(new BorderLayout());
		this.connectionTab = connectionTab;
		this.cardLayout = new CardLayout();
		this.settingsPanel = new JPanel(this.cardLayout);
		this.connectionControls = new ConnectionControls(this);
		setPreferredSize(new Dimension(720, 600));

		add(this.settingsPanel, BorderLayout.CENTER);
		add(this.connectionControls, BorderLayout.SOUTH);

		addCard(BLANK_CARD, new JPanel());
	}

	public final ConnectionTab getConnectionTab() {
		return this.connectionTab;
	}

	public final ConnectionControls getConnectionControls() {
		return this.connectionControls;
	}

	public void start() {
		getConnectionTab().addProviderItemListener(this);
		getConnectionControls().start();
	}

	public void stop() {
		getConnectionControls().stop();
	}

	public void addCard(String cardName, JComponent component) {
		this.settingsPanel.add(component, cardName);
	}

	public void showCard(String cardName) {
		this.cardLayout.show(this.settingsPanel, cardName);
	}

	@Override
	public void itemSelected(RfProviderItem<?> item) {
		item.select();
	}

	@Override
	public void itemDeselected(RfProviderItem<?> item) {
		showCard(BLANK_CARD);
	}

}
