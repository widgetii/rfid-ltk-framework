package ru.aplix.ltk.tester.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;


class TesterContent extends JPanel {

	private static final long serialVersionUID = -6862337278019842374L;

	private final TesterFrame frame;
	private final ConnectionTab connectionTab;
	private final ProgressTab progressTab;
	private final LogTab logTab;

	TesterContent(TesterFrame frame) {
		super(new BorderLayout());
		this.frame = frame;
		this.connectionTab = new ConnectionTab(this);
		this.progressTab = new ProgressTab(this);
		this.logTab = new LogTab();

		final JTabbedPane tabs = new JTabbedPane();

		tabs.addTab("Соединение", this.connectionTab);
		tabs.addTab("Состояние", this.progressTab);
		tabs.addTab("Лог", this.logTab);

		add(tabs, BorderLayout.CENTER);
	}

	public final TesterFrame getFrame() {
		return this.frame;
	}

	public final ConnectionTab getConnectionTab() {
		return this.connectionTab;
	}

	public final ProgressTab getProgressTab() {
		return this.progressTab;
	}

	public final LogTab getLogTab() {
		return this.logTab;
	}

	public void start() {
		getConnectionTab().start();
	}

	public void stop() {
		getConnectionTab().stop();
	}

}
