package ru.aplix.ltk.driver.ctg.ui;

import javax.swing.JComponent;

import ru.aplix.ltk.driver.ctg.CtgRfConnector;
import ru.aplix.ltk.driver.ctg.CtgRfProvider;
import ru.aplix.ltk.ui.RfConnectorUI;
import ru.aplix.ltk.ui.RfConnectorUIContext;


public class CtgRfConnectorUI implements RfConnectorUI {

	private final RfConnectorUIContext<CtgRfProvider> context;
	private CtgRfConnector connector;
	private CtgRfSettingsUI settingsUI;

	public CtgRfConnectorUI(RfConnectorUIContext<CtgRfProvider> context) {
		this.context = context;
		this.connector = context.getRfProvider().newConnector();
	}

	public final RfConnectorUIContext<CtgRfProvider> getContext() {
		return this.context;
	}

	public final CtgRfConnector getConnector() {
		return this.connector;
	}

	@Override
	public JComponent getSettingsUI() {
		if (this.settingsUI != null) {
			return this.settingsUI;
		}
		return this.settingsUI = new CtgRfSettingsUI(this);
	}

	@Override
	public CtgRfConnector buildConnector() {
		if (this.settingsUI != null) {
			this.settingsUI.configureConnector();
		}
		return this.connector;
	}

}
