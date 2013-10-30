package ru.aplix.ltk.store.web.receiver;

import static ru.aplix.ltk.store.web.receiver.RfReceiverBean.rfReceiverBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfTagEvent;


@Controller("rfTagController")
public class RfTagController {

	private static final int DEFAULT_PAGE_SIZE = 50;

	@Autowired
	private RfStore rfStore;

	@RequestMapping(
			value = "/tags/since.json",
			method = RequestMethod.GET,
			params = "receiver")
	@ResponseBody
	public List<RfTagBean> receiverTagsSince(
			@RequestParam("receiver") int receiverId,
			@RequestParam(value = "since", required = false) long since,
			@RequestParam(value = "limit", required = false) int limit) {

		final RfReceiver<?> receiver = this.rfStore.rfReceiverById(receiverId);
		final List<? extends RfTagAppearanceMessage> events =
				receiver.eventsSince(since, limit);

		return receiverTagBeans(receiver, events);
	}

	@RequestMapping(value = "/tags/list.json", method = RequestMethod.GET)
	@ResponseBody
	public List<RfTagBean> receiverTags(
			@RequestParam("receiver") int receiverId,
			@RequestParam(value = "fromId", required = false) long fromId,
			@RequestParam(value = "limit", required = false) int limit) {

		final RfReceiver<?> receiver = this.rfStore.rfReceiverById(receiverId);
		final List<? extends RfTagAppearanceMessage> events =
				receiver.loadEvents(fromId, limit);

		return receiverTagBeans(receiver, events);
	}

	@RequestMapping(
			value = "/tags/since.json",
			method = RequestMethod.GET)
	@ResponseBody
	public List<RfTagBean> allTagsSince(
			@RequestParam(value = "since", required = false) long since,
			@RequestParam(value = "offset", required = false) int offset,
			@RequestParam(value = "limit", required = false) int limit) {

		final List<? extends RfTagEvent> events = this.rfStore.allEventsSince(
				since,
				offset > 0 ? offset : 0,
				limit > 0 ? limit : DEFAULT_PAGE_SIZE);
		final HashMap<Integer, RfReceiverBean> receivers = new HashMap<>();
		final ArrayList<RfTagBean> beans = new ArrayList<>(events.size());

		for (RfTagEvent event : events) {
			beans.add(new RfTagBean(
					eventReceiverBean(receivers, event),
					event));
		}

		return beans;
	}

	private static List<RfTagBean> receiverTagBeans(
			RfReceiver<?> receiver,
			List<? extends RfTagAppearanceMessage> events) {

		final RfReceiverBean receiverBean = rfReceiverBean(receiver);
		final ArrayList<RfTagBean> beans = new ArrayList<>(events.size());

		for (RfTagAppearanceMessage event : events) {
			beans.add(new RfTagBean(receiverBean, event));
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
