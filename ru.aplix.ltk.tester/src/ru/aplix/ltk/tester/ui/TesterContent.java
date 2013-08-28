package ru.aplix.ltk.tester.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;


class TesterContent extends JPanel {

	private static final long serialVersionUID = -6862337278019842374L;

	private final TesterFrame frame;

	TesterContent(TesterFrame frame) {
		super(new BorderLayout());
		this.frame = frame;
		setPreferredSize(new Dimension(800, 600));
	}

	public final TesterFrame getFrame() {
		return this.frame;
	}

}
