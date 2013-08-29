package ru.aplix.ltk.tester.ui;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;


class ConnectionTab extends JSplitPane {

	private static final long serialVersionUID = 8471521708958485998L;

	private final RfProvidersModel providers;
	private final ConnectionSettings settings;

	ConnectionTab(TesterContent content) {
		this.providers = new RfProvidersModel(content);
		this.settings = new ConnectionSettings(this);

		final JScrollPane listScroll =
				new JScrollPane(new JList<>(this.providers));

		listScroll.setPreferredSize(new Dimension(200, 600));

		setLeftComponent(listScroll);
		setRightComponent(this.settings);
	}

	public final RfProvidersModel getProviders() {
		return this.providers;
	}

	public final ConnectionSettings getSettings() {
		return this.settings;
	}

	public void start() {
		getProviders().start();
	}

	public void stop() {
		getProviders().stop();
	}

}
