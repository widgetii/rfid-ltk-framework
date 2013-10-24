package ru.aplix.ltk.tester.ui;

import static javax.swing.JSplitPane.VERTICAL_SPLIT;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;


class ProgressTab extends JPanel {

	private static final long serialVersionUID = -5756411271969549347L;

	private final TesterContent content;
	private final TagsPanel tags;
	private final LogPanel log;
	private final JButton clearButton;

	ProgressTab(TesterContent content) {
		super(new BorderLayout());
		this.content = content;
		this.tags = new TagsPanel(this);
		this.log = new LogPanel();
		this.clearButton = new JButton(new AbstractAction("Очистить лог") {
			private static final long serialVersionUID = 6716153141110892161L;
			@Override
			public void actionPerformed(ActionEvent e) {
				getLog().clear();
			}
		});

		final JSplitPane statusPane = new JSplitPane(VERTICAL_SPLIT);

		statusPane.setLeftComponent(this.tags);
		statusPane.setRightComponent(this.log);

		add(statusPane, BorderLayout.CENTER);

		final JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		toolbar.add(this.clearButton);
		add(toolbar, BorderLayout.SOUTH);
	}

	public final TesterContent getContent() {
		return this.content;
	}

	public final TagsPanel getTags() {
		return this.tags;
	}

	public final LogPanel getLog() {
		return this.log;
	}

}
