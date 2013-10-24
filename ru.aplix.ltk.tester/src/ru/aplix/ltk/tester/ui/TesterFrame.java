package ru.aplix.ltk.tester.ui;

import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.osgi.framework.BundleContext;


public class TesterFrame extends JFrame {

	private static final long serialVersionUID = -6331362881299046299L;
	private final BundleContext bundleContext;
	private final TesterContent content;

	public TesterFrame(BundleContext context) throws HeadlessException {
		super("Тестирование RFID");
		this.bundleContext = context;
		this.content = new TesterContent(this);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		setContentPane(this.content);
		pack();
	}

	public final BundleContext getBundleContext() {
		return this.bundleContext;
	}

	public final TesterContent getContent() {
		return this.content;
	}

	public void start() {
		getContent().start();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(true);
			}
		});
	}

	public void stop() {
		getContent().stop();
		if (isVisible()) {
			dispose();
		}
	}

}
