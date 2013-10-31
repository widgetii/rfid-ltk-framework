package ru.aplix.ltk.store.web.receiver;

import static ru.aplix.ltk.store.web.receiver.RfReceiverBean.rfReceiverBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfTagEvent;
import ru.aplix.ltk.store.RfTagQuery;


public class FoundRfTagsBean {

	private final List<RfTagBean> events;
	private final long totalCount;

	public FoundRfTagsBean(RfTagQuery query) {
		this.events = eventBeans(query.find());
		this.totalCount = query.getTotalCount();
	}

	public List<RfTagBean> getEvents() {
		return this.events;
	}

	public long getTotalCount() {
		return this.totalCount;
	}

	private static ArrayList<RfTagBean> eventBeans(
			final List<? extends RfTagEvent> events) {

		final HashMap<Integer, RfReceiverBean> receivers = new HashMap<>();
		final ArrayList<RfTagBean> beans = new ArrayList<>(events.size());

		for (RfTagEvent event : events) {
			beans.add(new RfTagBean(
					eventReceiverBean(receivers, event),
					event));
		}

		return beans;
	}

	private static RfReceiverBean eventReceiverBean(
			HashMap<Integer, RfReceiverBean> receivers,
			RfTagEvent event) {

		final RfReceiver<?> receiver = event.getReceiver();
		final Integer receiverId = receiver.getId();
		final RfReceiverBean found = receivers.get(receiverId);

		if (found != null) {
			return found;
		}

		final RfReceiverBean receiverBean = rfReceiverBean(receiver);

		receivers.put(receiverId, receiverBean);

		return receiverBean;
	}

}
