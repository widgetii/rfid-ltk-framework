package ru.aplix.ltk.tester.ui;

import static ru.aplix.ltk.tester.ui.UIUtil.invokeInUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;


class LogTab extends JPanel {

	private static final SimpleDateFormat TIME_FORMAT =
			new SimpleDateFormat("[HH:mm:ss] ");

	private static final long serialVersionUID = 434922263480668561L;

	private final JTextArea log;
	private final JButton clearButton;

	LogTab() {
		super(new BorderLayout());
		this.log = new JTextArea();
		this.clearButton = new JButton(new AbstractAction("Очистить лог") {
			private static final long serialVersionUID = 6716153141110892161L;
			@Override
			public void actionPerformed(ActionEvent e) {
				LogTab.this.log.setText("");
			}
		});

		this.log.setEditable(false);

		final JScrollPane scrollPane = new JScrollPane(this.log);

		scrollPane.setPreferredSize(new Dimension(800, 600));
		add(scrollPane, BorderLayout.CENTER);

		final JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		toolbar.add(this.clearButton);
		add(toolbar, BorderLayout.SOUTH);
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

		final StringBuilder out = new StringBuilder(message.length() + 12);

		out.append(TIME_FORMAT.format(new Date()));
		out.append(message);
		out.append('\n');

		this.log.append(out.toString());
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
