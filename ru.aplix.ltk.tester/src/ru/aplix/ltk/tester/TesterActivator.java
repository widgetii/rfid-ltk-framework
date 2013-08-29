package ru.aplix.ltk.tester;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ru.aplix.ltk.tester.ui.TesterFrame;


public class TesterActivator implements BundleActivator {

	private TesterFrame frame;

	@Override
	public void start(BundleContext context) throws Exception {
		this.frame = new TesterFrame(context);
		this.frame.start();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		this.frame.stop();
	}

}
