package ru.aplix.ltk.tester;

import static javax.swing.SwingUtilities.invokeLater;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ru.aplix.ltk.tester.ui.TesterFrame;


public class TesterActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {

		final TesterFrame frame = new TesterFrame(context);

		invokeLater(frame);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}