package ru.aplix.ltk.tester.ui;

import static javax.swing.SwingUtilities.invokeLater;
import static ru.aplix.ltk.ui.RfProviderUI.RF_PROVIDER_UI_SELECTOR;

import java.util.IdentityHashMap;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.ui.RfProviderUI;


public class RfProvidersModel extends AbstractListModel<RfProviderItem> {

	private static final long serialVersionUID = -5122285392607147264L;

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static final Class<RfProviderUI<?>> PROVIDER_UI_CLASS =
			(Class) RfProviderUI.class;

	private final ConnectionTab tab;
	private final BundleContext context;
	private final ServiceTracker<RfProvider, RfProvider> providersTracker;
	private final ServiceTracker<RfProviderUI<?>, RfProviderUI<?>> uiTracker;
	private final IdentityHashMap<RfProvider, RfProviderItem> itemsByProvider =
			new IdentityHashMap<>();
	private final TreeSet<RfProviderItem> itemSet = new TreeSet<>();
	private RfProviderItem[] itemList = new RfProviderItem[0];
	private int lastIndex;


	public RfProvidersModel(ConnectionTab tab) {
		this.tab = tab;
		this.context = tab.getContent().getFrame().getBundleContext();
		this.providersTracker = new ServiceTracker<>(
				this.context,
				RfProvider.class,
				new RfProvidersTracker(this));
		this.uiTracker = new ServiceTracker<>(
				this.context,
				PROVIDER_UI_CLASS,
				new RfProviderUITracker(this));
	}

	public void start() {
		this.uiTracker.open();
		this.providersTracker.open();
	}

	public void stop() {
		this.providersTracker.close();
		this.uiTracker.close();
	}

	@Override
	public int getSize() {
		return this.itemList.length;
	}

	@Override
	public RfProviderItem getElementAt(int index) {
		return this.itemList[index];
	}

	RfProviderUI<?> providerUI(RfProvider provider) {
		return RF_PROVIDER_UI_SELECTOR.selectHandlerFor(
				provider,
				this.uiTracker.getServices(new RfProviderUI<?>[0]));
	}

	private synchronized void addProvider(RfProvider provider) {
		if (this.itemsByProvider.containsKey(provider)) {
			return;
		}

		final RfProviderItem item =
				new RfProviderItem(this.tab, provider, ++this.lastIndex);

		this.itemsByProvider.put(provider, item);
		this.itemSet.add(item);
		refreshItems();
	}

	private synchronized void removeProvider(RfProvider provider) {

		final RfProviderItem item = this.itemsByProvider.remove(provider);

		if (item == null) {
			return;
		}

		this.itemSet.remove(item);
		item.dispose();
		refreshItems();
	}

	private void refreshItems() {
		invokeLater(new Runnable() {
			@Override
			public void run() {
				reorderItems();
			}
		});
	}

	private void refreshUI() {
		invokeLater(new Runnable() {
			@Override
			public void run() {
				updateUI();
			}
		});
	}

	private synchronized void updateUI() {
		for (RfProviderItem item : this.itemList) {
			item.updateUI();
		}
	}

	private synchronized void reorderItems() {

		final RfProviderItem[] newItems =
				this.itemSet.toArray(new RfProviderItem[this.itemSet.size()]);
		final RfProviderItem[] oldItems = this.itemList;

		this.itemList = newItems;

		if (oldItems.length > 0) {
			fireIntervalRemoved(this, 0, oldItems.length - 1);
		}
		if (newItems.length > 0) {
			fireIntervalAdded(this, 0, newItems.length - 1);
		}
	}

	private static final class RfProvidersTracker
			implements ServiceTrackerCustomizer<RfProvider, RfProvider> {

		private final RfProvidersModel model;

		RfProvidersTracker(RfProvidersModel model) {
			this.model = model;
		}

		@Override
		public RfProvider addingService(
				ServiceReference<RfProvider> reference) {

			final RfProvider provider =
					this.model.context.getService(reference);

			this.model.addProvider(provider);

			return provider;
		}

		@Override
		public void modifiedService(
				ServiceReference<RfProvider> reference,
				RfProvider service) {
		}

		@Override
		public void removedService(
				ServiceReference<RfProvider> reference,
				RfProvider service) {
			this.model.context.ungetService(reference);
			this.model.removeProvider(service);
		}

	}

	private static final class RfProviderUITracker
			implements ServiceTrackerCustomizer<
					RfProviderUI<?>,
					RfProviderUI<?>> {

		private final RfProvidersModel model;

		RfProviderUITracker(RfProvidersModel model) {
			this.model = model;
		}

		@Override
		public RfProviderUI<?> addingService(
				ServiceReference<RfProviderUI<?>> reference) {

			final RfProviderUI<?> provider =
					this.model.context.getService(reference);

			this.model.refreshUI();

			return provider;
		}

		@Override
		public void modifiedService(
				ServiceReference<RfProviderUI<?>> reference,
				RfProviderUI<?> service) {
		}

		@Override
		public void removedService(
				ServiceReference<RfProviderUI<?>> reference,
				RfProviderUI<?> service) {
			this.model.context.ungetService(reference);
			this.model.refreshUI();
		}

	}

}
