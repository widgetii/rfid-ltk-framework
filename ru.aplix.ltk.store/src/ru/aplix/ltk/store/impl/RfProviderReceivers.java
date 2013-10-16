package ru.aplix.ltk.store.impl;

import java.util.HashSet;

import ru.aplix.ltk.core.RfProvider;


final class RfProviderReceivers {

	private final RfStoreImpl store;
	private final RfProvider<?> provider;
	private final HashSet<Integer> receiverIds = new HashSet<>();

	RfProviderReceivers(RfStoreImpl store, RfProvider<?> provider) {
		this.store = store;
		this.provider = provider;
	}

	public final RfStoreImpl getRfStore() {
		return this.store;
	}

	public final RfProvider<?> getRfProvider() {
		return this.provider;
	}

	public void load() {
		getRfStore().getExecutor().submit(new Runnable() {
			@Override
			public void run() {
				try {
					getRfStore().getMonitoring().loadReceivers(
							"Load receivers for " + RfProviderReceivers.this);
					getRfStore().requestProviderReceivers(getRfProvider());
				} catch (Throwable e) {
					getRfStore().getMonitoring().failedToLoadReceivers(
							"Failed to load receivers for "
							+ RfProviderReceivers.this,
							e);
				}
			}
		});
	}

	public void add(RfReceiverImpl<?> receiver) {
		synchronized (this.receiverIds) {
			this.receiverIds.remove(receiver.getId());
		}
	}

	public void remove(RfReceiverImpl<?> receiver) {
		synchronized (this.receiverIds) {
			this.receiverIds.remove(receiver.getId());
		}
	}

	public void shutdown() {

		final Integer[] ids;

		synchronized (this.receiverIds) {
			ids = this.receiverIds.toArray(
					new Integer[this.receiverIds.size()]);
			this.receiverIds.clear();
		}

		getRfStore().getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				shutdownReceivers(ids);
			}
		});
	}

	@Override
	public String toString() {
		if (this.provider == null) {
			return super.toString();
		}
		return this.provider.getName() + " (" + this.provider.getId() + ')';
	}

	private void shutdownReceivers(Integer[] ids) {
		for (Integer id : ids) {

			final RfReceiverImpl<?> removed = getRfStore().removeReceiver(id);

			if (removed != null) {
				removed.shutdown();
			}
		}
	}

}