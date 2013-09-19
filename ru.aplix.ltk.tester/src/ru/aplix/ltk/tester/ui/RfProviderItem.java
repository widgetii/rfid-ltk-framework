package ru.aplix.ltk.tester.ui;

import static javax.swing.BorderFactory.createTitledBorder;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.ui.RfProviderUI;
import ru.aplix.ltk.ui.RfSettingsUI;
import ru.aplix.ltk.ui.RfSettingsUIContext;


public class RfProviderItem<S extends RfSettings>
		implements Comparable<RfProviderItem<?>> {

	private final ConnectionTab tab;
	private final RfProvider<S> provider;
	private final String name;
	private final int index;
	private RfProviderUI<S> providerUI;
	private RfSettingsUI<S> settingsUI;
	private JComponent uiComponent;

	RfProviderItem(
			ConnectionTab tab,
			RfProvider<S> provider,
			int index) {
		this.tab = tab;
		this.provider = provider;
		this.name = providerName(provider, index);
		this.index = index;
	}

	public final String getName() {
		return this.name;
	}

	public final RfProvider<S> getProvider() {
		return this.provider;
	}

	public final boolean isSelected() {
		return this.tab.getSelected() == this;
	}

	public final RfProviderUI<S> getProviderUI() {
		if (this.providerUI != null) {
			return this.providerUI;
		}
		return this.providerUI = findProviderUI();
	}

	public final RfSettingsUI<S> getSettingsUI() {
		if (this.settingsUI != null) {
			return this.settingsUI;
		}
		return this.settingsUI =
				getProviderUI().newSettingsUI(new UIContext());
	}

	public Component getUIComponent() {
		if (this.settingsUI != null) {
			return this.uiComponent;
		}

		final JComponent settingsUI = getSettingsUI().getUIComponent();

		if (settingsUI == null) {
			// Blank UI.
			this.uiComponent = new JPanel();
		} else {
			this.uiComponent = settingsUI;
		}

		this.uiComponent.setBorder(
				createTitledBorder(getName() + ": настройки соединения"));

		final ConnectionSettings settings = this.tab.getSettings();
		final String cardName = cardName();

		settings.addCard(cardName, this.uiComponent);

		return this.uiComponent;
	}

	public void updateUI() {
		if (this.providerUI == null) {
			return;
		}

		final RfProviderUI<S> providerUI = findProviderUI();

		if (providerUI == this.providerUI) {
			return;
		}

		disposeUI();
		this.providerUI = providerUI;
		this.settingsUI = null;
		this.settingsUI = null;
		if (isSelected()) {
			select();
		}
	}

	public void select() {
		getUIComponent();// Construct UI if not constructed yet.
		this.tab.getSettings().showCard(cardName());
	}

	public RfConnection connect() {

		final S settings = getSettingsUI().buildSettings();

		return getProvider().connect(settings);
	}

	public void dispose() {
		disposeUI();
	}

	@Override
	public int compareTo(RfProviderItem<?> o) {

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

		final RfProviderItem<?> other = (RfProviderItem<?>) obj;

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

	private static String providerName(RfProvider<?> provider, int index) {

		final String name = provider.getName();

		if (name != null) {
			return name;
		}

		return "#" + index;
	}

	private final String cardName() {
		return Integer.toString(this.index);
	}

	private RfProviderUI<S> findProviderUI() {
		return this.tab.getProviders().providerUI(getProvider());
	}

	private void disposeUI() {
		if (this.uiComponent != null) {

			final Container parent = this.uiComponent.getParent();

			if (parent != null) {
				parent.remove(this.uiComponent);
			}
		}
	}

	private final class UIContext implements RfSettingsUIContext<S> {

		@Override
		public RfProvider<S> getRfProvider() {
			return getProvider();
		}

	}

}
