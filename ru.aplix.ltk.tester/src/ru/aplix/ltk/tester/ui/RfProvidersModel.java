package ru.aplix.ltk.tester.ui;

import static javax.swing.SwingUtilities.invokeLater;

import java.util.IdentityHashMap;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import ru.aplix.ltk.core.RfProvider;


public class RfProvidersModel extends AbstractListModel<RfProviderItem> {

	private static final long serialVersionUID = -5122285392607147264L;

	private final BundleContext context;
	private final ServiceTracker<RfProvider, RfProvider> providersTracker;
	private final IdentityHashMap<RfProvider, RfProviderItem> itemsByProvider =
			new IdentityHashMap<>();
	private final TreeSet<RfProviderItem> itemSet = new TreeSet<>();
	private RfProviderItem[] itemList = new RfProviderItem[0];
	private int lastIndex;

	public RfProvidersModel(TesterContent content) {
		this.context = content.getFrame().getBundleContext();
		this.providersTracker = new ServiceTracker<>(
				this.context,
				RfProvider.class,
				new RfProvidersTracker(this));
	}

	public void start() {
		this.providersTracker.open();
	}

	public void stop() {
		this.providersTracker.close();
	}

	@Override
	public int getSize() {
		return this.itemList.length;
	}

	@Override
	public RfProviderItem getElementAt(int index) {
		return this.itemList[index];
	}

	private synchronized void addProvider(RfProvider provider) {
		if (this.itemsByProvider.containsKey(provider)) {
			return;
		}

		final RfProviderItem item =
				new RfProviderItem(provider, ++this.lastIndex);

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

	private synchronized void reorderItems() {

		final RfProviderItem[] newItems = orderItems();
		final RfProviderItem[] oldItems = this.itemList;

		this.itemList = newItems;

		if (oldItems.length > 0) {
			fireIntervalRemoved(this, 0, oldItems.length - 1);
		}
		if (newItems.length > 0) {
			fireIntervalAdded(this, 0, newItems.length - 1);
		}
	}

	private RfProviderItem[] orderItems() {

		final RfProviderItem[] newItemList =
				new RfProviderItem[this.itemSet.size()];
		int i = 0;

		for (RfProviderItem item : this.itemSet) {
			item.setItemIndex(i);
			newItemList[i++] = item;
		}

		return newItemList;
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

}
