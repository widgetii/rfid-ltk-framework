package ru.aplix.ltk.tester.ui;

import static ru.aplix.ltk.tester.ui.UIUtil.invokeInUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


class LogTab extends JPanel {

	private static final long serialVersionUID = 434922263480668561L;

	private final JTextArea log;

	LogTab() {
		super(new BorderLayout());
		this.log = new JTextArea();

		this.log.setEditable(false);

		final JScrollPane scrollPane = new JScrollPane(this.log);

		scrollPane.setPreferredSize(new Dimension(800, 600));
		add(scrollPane, BorderLayout.CENTER);
	}

	public void log(final String message) {
		invokeInUI(new Runnable() {
			@Override
			public void run() {
				append(message);
			}
		});
	}

	public void log(Throwable error) {
		log(errorToString(error));
	}

	public void append(String message) {
		this.log.append(message + "\n");
	}

	public void append(Throwable error) {
		append(errorToString(error));
	}

	private static String errorToString(Throwable error) {

		final StringWriter result = new StringWriter();

		try (PrintWriter out = new PrintWriter(result)) {
			error.printStackTrace(out);
		}

		return result.toString();
	}

}
