package ru.aplix.ltk.tester.ui;

import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;


public class TesterFrame extends JFrame implements Runnable {

	private static final long serialVersionUID = 4960622669104423471L;

	private final BundleContext context;
	private final TesterContent content;

	public TesterFrame(BundleContext context) throws HeadlessException {
		super("Тестирование RFID");
		this.context = context;
		this.content = new TesterContent(this);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				stopApplication();
			}
		});
		setContentPane(this.content);
	}

	public final TesterContent getContent() {
		return this.content;
	}

	@Override
	public void run() {
		pack();
		setVisible(true);
	}

	private void stopApplication() {

		final Bundle systemBundle = this.context.getBundle(0);

		if (systemBundle != null) {
			try {
				systemBundle.stop();
			} catch (BundleException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

}
