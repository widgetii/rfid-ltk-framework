package ru.aplix.ltk.store.web.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.aplix.ltk.store.RfTagEvent;
import ru.aplix.ltk.store.RfTagQuery;


public class FoundRfTagsBean {

	private final List<RfTagEventBean> events;
	private final long totalCount;

	public FoundRfTagsBean(RfTagQuery query) {
		this.events = eventBeans(query.find());
		this.totalCount = query.getTotalCount();
	}

	public List<RfTagEventBean> getEvents() {
		return this.events;
	}

	public long getTotalCount() {
		return this.totalCount;
	}

	private static ArrayList<RfTagEventBean> eventBeans(
			final List<? extends RfTagEvent> events) {

		final HashMap<Integer, RfReceiverDesc> receivers = new HashMap<>();
		final ArrayList<RfTagEventBean> beans = new ArrayList<>(events.size());

		for (RfTagEvent event : events) {
			beans.add(new RfTagEventBean(
					eventReceiverBean(receivers, event),
					event));
		}

		return beans;
	}

	private static RfReceiverDesc eventReceiverBean(
			HashMap<Integer, RfReceiverDesc> receivers,
			RfTagEvent event) {

		final Integer receiverId = event.getReceiverId();
		final RfReceiverDesc found = receivers.get(receiverId);

		if (found != null) {
			return found;
		}

		final RfReceiverDesc receiverDesc =
				RfReceiverDesc.rfReceiverDesc(receiverId, event.getReceiver());

		receivers.put(receiverId, receiverDesc);

		return receiverDesc;
	}

}
