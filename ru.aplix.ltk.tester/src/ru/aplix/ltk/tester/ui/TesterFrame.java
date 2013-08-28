package ru.aplix.ltk.tester.ui;

import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.osgi.framework.BundleContext;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.util.tracker.ServiceTracker;


public class TesterFrame extends JFrame implements Runnable {

	private static final long serialVersionUID = 4960622669104423471L;

	private final ServiceTracker<
			ApplicationHandle,
			ApplicationHandle> appTracker;
	private final TesterContent content;

	public TesterFrame(BundleContext context) throws HeadlessException {
		super("Тестирование RFID");
		this.appTracker = new ServiceTracker<>(
				context,
				ApplicationHandle.class,
				null);
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

		final ApplicationHandle appHandle = this.appTracker.getService();

		if (appHandle != null) {
			appHandle.destroy();
		}
	}

}
