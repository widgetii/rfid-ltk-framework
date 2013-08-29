package ru.aplix.ltk.tester.ui;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


class ConnectionTab extends JSplitPane {

	private static final long serialVersionUID = 8471521708958485998L;

	private final TesterContent content;
	private final RfProvidersModel providers;
	private final ConnectionSettings settings;
	private final JList<RfProviderItem> providersList;
	private final EventListenerList providerListeners = new EventListenerList();
	private RfProviderItem selected;


	ConnectionTab(TesterContent content) {
		this.content = content;
		this.providers = new RfProvidersModel(this);
		this.providersList = new JList<>(this.providers);
		this.settings = new ConnectionSettings(this);

		this.providersList.setSelectionMode(SINGLE_SELECTION);
		this.providersList.addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						setSelection(e.getFirstIndex());
					}
				});

		final JScrollPane listScroll =
				new JScrollPane(this.providersList);

		listScroll.setPreferredSize(new Dimension(200, 600));

		setLeftComponent(listScroll);
		setRightComponent(this.settings);
	}

	public final TesterContent getContent() {
		return this.content;
	}

	public final RfProvidersModel getProviders() {
		return this.providers;
	}

	public final JList<RfProviderItem> getProvidersList() {
		return this.providersList;
	}

	public final ConnectionSettings getSettings() {
		return this.settings;
	}

	public final RfProviderItem getSelected() {
		return this.selected;
	}

	public void start() {
		getProviders().start();
		getSettings().start();
		this.selected = getProvidersList().getSelectedValue();
	}

	public void stop() {
		getProviders().stop();
		getSettings().stop();
	}

	public void addProviderItemListener(RfProviderItemListener listener) {
		this.providerListeners.add(RfProviderItemListener.class, listener);
	}

	public void removeProviderItemListener(RfProviderItemListener listener) {
		this.providerListeners.remove(RfProviderItemListener.class, listener);
	}

	private void setSelection(int index) {

		final RfProviderItem selected;
		final int size = getProviders().getSize();

		if (index >= size || size == 0) {
			selected = null;
		} else {
			selected = this.providers.getElementAt(index);
		}

		final RfProviderItem deselected = this.selected;

		if (deselected == selected) {
			return;
		}

		this.selected = selected;
		if (deselected != null) {
			for (RfProviderItemListener listener
					: this.providerListeners.getListeners(
							RfProviderItemListener.class)) {
				listener.itemDeselected(deselected);
			}
		}
		if (selected != null) {
			for (RfProviderItemListener listener
					: this.providerListeners.getListeners(
							RfProviderItemListener.class)) {
				listener.itemSelected(selected);
			}
		}
	}

}
