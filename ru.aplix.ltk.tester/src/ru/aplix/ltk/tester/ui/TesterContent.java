package ru.aplix.ltk.tester.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;


class TesterContent extends JPanel {

	private static final long serialVersionUID = -6862337278019842374L;

	private final TesterFrame frame;

	TesterContent(TesterFrame frame) {
		super(new BorderLayout());
		this.frame = frame;
	}

	public final TesterFrame getFrame() {
		return this.frame;
	}

}
