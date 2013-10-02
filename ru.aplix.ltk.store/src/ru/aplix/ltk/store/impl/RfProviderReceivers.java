package ru.aplix.ltk.store.impl;

import static org.osgi.service.log.LogService.LOG_ERROR;

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

	public final RfStoreImpl getStore() {
		return this.store;
	}

	public final RfProvider<?> getProvider() {
		return this.provider;
	}

	public void load() {
		getStore().getExecutor().submit(new Runnable() {
			@Override
			public void run() {
				try {
					getStore().loadProviderReceivers(getProvider());
				} catch (Throwable e) {
					getStore().log().log(
							LOG_ERROR,
							"Failed to load receivers for " + this,
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

		getStore().getExecutor().execute(new Runnable() {
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
		return this.provider.getId() + " (" + this.provider.getName() + ')';
	}

	private void shutdownReceivers(Integer[] ids) {
		for (Integer id : ids) {

			final RfReceiverImpl<?> removed = getStore().removeReceiver(id);

			if (removed != null) {
				removed.shutdown();
			}
		}
	}

}