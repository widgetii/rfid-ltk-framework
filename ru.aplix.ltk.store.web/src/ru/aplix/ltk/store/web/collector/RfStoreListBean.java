package ru.aplix.ltk.store.web.collector;

import java.util.ArrayList;
import java.util.Collection;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.store.RfReceiver;


public class RfStoreListBean {

	private final ArrayList<RfStoreBean> stores;

	public RfStoreListBean(
			Collection<? extends RfReceiver<?>> stores) {
		this.stores = new ArrayList<>(stores.size());
		for (RfReceiver<?> store : stores) {

			@SuppressWarnings("unchecked")
			final RfStoreBean bean =
					new RfStoreBean((RfReceiver<HttpRfSettings>) store);

			this.stores.add(bean);
		}
	}

	public ArrayList<RfStoreBean> getStores() {
		return this.stores;
	}

}
