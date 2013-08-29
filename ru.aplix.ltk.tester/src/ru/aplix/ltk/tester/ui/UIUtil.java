package ru.aplix.ltk.tester.ui;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;


public class UIUtil {

	public static void invokeInUI(Runnable action) {
		if (isEventDispatchThread()) {
			action.run();
		} else {
			invokeLater(action);
		}
	}

	private UIUtil() {
	}

}
