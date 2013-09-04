package ru.aplix.ltk.driver.ctg.ui;

import javax.swing.JComponent;

import ru.aplix.ltk.driver.ctg.CtgRfSettings;
import ru.aplix.ltk.ui.RfSettingsUI;
import ru.aplix.ltk.ui.RfSettingsUIContext;


public class CtgRfSettingsUI implements RfSettingsUI<CtgRfSettings> {

	private final RfSettingsUIContext<CtgRfSettings> context;
	private CtgRfSettings settings;
	private CtgRfSettingsPanel uiComponent;

	public CtgRfSettingsUI(RfSettingsUIContext<CtgRfSettings> context) {
		this.context = context;
		this.settings = context.getRfProvider().newSettings();
	}

	public final RfSettingsUIContext<CtgRfSettings> getContext() {
		return this.context;
	}

	public final CtgRfSettings getSettings() {
		return this.settings;
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
			this.uiComponent.configureConnector();
		}
		return this.settings;
	}

}
