package ru.aplix.ltk.tester.ui;

import java.awt.CardLayout;

import javax.swing.JPanel;


class ConnectionSettings extends JPanel implements RfProviderItemListener {

	private static final long serialVersionUID = -8803090880315132382L;

	private static final String BLANK_CARD = "blank";

	private final ConnectionTab connectionTab;
	private final CardLayout cardLayout;
	private final JPanel blank;

	ConnectionSettings(ConnectionTab connectionTab) {
		this.connectionTab = connectionTab;
		this.cardLayout = new CardLayout();
		this.blank = new JPanel();

		setLayout(this.cardLayout);
		add(this.blank, BLANK_CARD);
	}

	public void start() {
		this.connectionTab.addProviderItemListener(this);
	}

	public void stop() {
	}

	public void showCard(String cardName) {
		this.cardLayout.show(this, cardName);
	}

	@Override
	public void itemSelected(RfProviderItem item) {
		item.select();
	}

	@Override
	public void itemDeselected(RfProviderItem item) {
		showCard(BLANK_CARD);
	}

}
