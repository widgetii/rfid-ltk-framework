package ru.aplix.ltk.tester.ui;

import static javax.swing.BorderFactory.createTitledBorder;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.ui.RfConnectorUI;
import ru.aplix.ltk.ui.RfConnectorUIContext;
import ru.aplix.ltk.ui.RfProviderUI;


public class RfProviderItem implements Comparable<RfProviderItem> {

	private final ConnectionTab tab;
	private final RfProvider provider;
	private final String name;
	private final int index;
	private RfProviderUI<?> providerUI;
	private RfConnectorUI connectorUI;
	private JComponent settingsUI;

	RfProviderItem(
			ConnectionTab tab,
			RfProvider provider,
			int index) {
		this.tab = tab;
		this.provider = provider;
		this.name = providerName(provider, index);
		this.index = index;
	}

	public final String getName() {
		return this.name;
	}

	public final RfProvider getProvider() {
		return this.provider;
	}

	public final boolean isSelected() {
		return this.tab.getSelected() == this;
	}

	public final RfProviderUI<?> getProviderUI() {
		if (this.providerUI != null) {
			return this.providerUI;
		}
		return this.providerUI = findProviderUI();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public final RfConnectorUI getConnectorUI() {
		if (this.connectorUI != null) {
			return this.connectorUI;
		}
		return this.connectorUI =
				getProviderUI().newConnectorUI(new UIContext());
	}

	public Component getSettingsUI() {
		if (this.settingsUI != null) {
			return this.settingsUI;
		}

		final JComponent settingsUI = getConnectorUI().getSettingsUI();

		if (settingsUI == null) {
			// Blank UI.
			this.settingsUI = new JPanel();
		} else {
			this.settingsUI = settingsUI;
		}
		this.settingsUI.setBorder(
				createTitledBorder(getName() + ": настройки соединения"));

		final ConnectionSettings settings = this.tab.getSettings();
		final String cardName = cardName();

		settings.addCard(cardName, this.settingsUI);

		return this.settingsUI;
	}

	public void updateUI() {
		if (this.providerUI == null) {
			return;
		}

		final RfProviderUI<?> providerUI = findProviderUI();

		if (providerUI == this.providerUI) {
			return;
		}

		disposeSettingsUI();
		this.providerUI = providerUI;
		this.connectorUI = null;
		this.settingsUI = null;
		if (isSelected()) {
			select();
		}
	}

	public void select() {
		getSettingsUI();// Construct UI if not constructed yet.
		this.tab.getSettings().showCard(cardName());
	}

	public void dispose() {
		disposeSettingsUI();
	}

	@Override
	public int compareTo(RfProviderItem o) {

		final int cmp = this.name.compareTo(o.name);

		if (cmp != 0) {
			return cmp;
		}

		return this.index - o.index;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + this.index;
		result = prime * result + this.name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final RfProviderItem other = (RfProviderItem) obj;

		if (this.index != other.index) {
			return false;
		}
		if (!this.name.equals(other.name)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		if (this.name == null) {
			return super.toString();
		}
		return this.name;
	}

	private static String providerName(RfProvider provider, int index) {

		final String name = provider.getName();

		if (name != null) {
			return name;
		}

		return "#" + index;
	}

	private final String cardName() {
		return Integer.toString(this.index);
	}

	private RfProviderUI<?> findProviderUI() {
		return this.tab.getProviders().providerUI(getProvider());
	}

	private void disposeSettingsUI() {
		if (this.settingsUI != null) {
			this.settingsUI.getParent().remove(this.settingsUI);
		}
	}

	private final class UIContext<P extends RfProvider>
			implements RfConnectorUIContext<P> {

		@SuppressWarnings("unchecked")
		@Override
		public P getRfProvider() {
			return (P) getProvider();
		}

	}

}
