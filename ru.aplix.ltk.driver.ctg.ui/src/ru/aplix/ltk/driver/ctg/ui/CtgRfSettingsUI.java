package ru.aplix.ltk.driver.ctg.ui;

import javax.swing.JComponent;

import ru.aplix.ltk.core.collector.*;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;
import ru.aplix.ltk.ui.RfSettingsUI;
import ru.aplix.ltk.ui.RfSettingsUIContext;


public class CtgRfSettingsUI implements RfSettingsUI<CtgRfSettings> {

	private final RfSettingsUIContext<CtgRfSettings> context;
	private final CtgRfSettings settings;
	private CtgRfSettingsPanel uiComponent;
	private long tagTransactionTimeout;
	private long tagInvalidationTimeout;

	public CtgRfSettingsUI(RfSettingsUIContext<CtgRfSettings> context) {
		this.context = context;
		this.settings = context.getRfProvider().newSettings();
		this.settings.setTrackingPolicy(new CtgUITrackingPolicy());

		final DefaultRfTracker tracker = new DefaultRfTracker();

		this.tagTransactionTimeout = tracker.getTransactionTimeout();
		this.tagInvalidationTimeout = tracker.getInvalidationTimeout();
	}

	public final RfSettingsUIContext<CtgRfSettings> getContext() {
		return this.context;
	}

	public final CtgRfSettings getSettings() {
		return this.settings;
	}

	public final long getTagTransactionTimeout() {
		return this.tagTransactionTimeout;
	}

	public final void setTagTransactionTimeout(long transactionTimeout) {
		this.tagTransactionTimeout = transactionTimeout;
	}

	public final long getTagInvalidationTimeout() {
		return this.tagInvalidationTimeout;
	}

	public final void setTagInvalidationTimeout(long invalidationTimeout) {
		this.tagInvalidationTimeout = invalidationTimeout;
	}

	@Override
	public JComponent getUIComponent() {
		if (this.uiComponent != null) {
			return this.uiComponent;
		}
		return this.uiComponent = new CtgRfSettingsPanel(this);
	}

	@Override
	public CtgRfSettings buildSettings() {
		if (this.uiComponent != null) {
			this.uiComponent.fillSettings();
		}
		return this.settings;
	}

	private final class CtgUITrackingPolicy implements RfTrackingPolicy {

		@Override
		public RfTracker newTracker(RfCollector collector) {

			final DefaultRfTracker tracker = new DefaultRfTracker();

			tracker.setTransactionTimeout(getTagTransactionTimeout());
			tracker.setInvalidationTimeout(getTagInvalidationTimeout());

			return tracker;
		}

	}

}
