package ru.aplix.ltk.tester.ui;

import static ru.aplix.ltk.tester.ui.UIUtil.invokeInUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


class LogPanel extends JPanel {

	private static final long serialVersionUID = -3760655588723351309L;

	private static final SimpleDateFormat TIME_FORMAT =
			new SimpleDateFormat("[HH:mm:ss] ");

	private final JTextArea log;

	LogPanel() {
		super(new BorderLayout());
		this.log = new JTextArea();

		this.log.setEditable(false);

		final JScrollPane scrollPane = new JScrollPane(this.log);

		scrollPane.setPreferredSize(new Dimension(800, 350));
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

	public void clear() {
		this.log.setText("");
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
