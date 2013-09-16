package ru.aplix.ltk.store.web.receiver;

import static java.util.Objects.requireNonNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfReceiverEditor;
import ru.aplix.ltk.store.RfStore;


@Controller("rfReceiverController")
public class RfReceiverController {

	@Autowired
	private RfStore rfStore;

	@Autowired
	@Qualifier("httpRfProvider")
	private RfProvider<HttpRfSettings> rfProvider;

	public final RfStore getRfStore() {
		return this.rfStore;
	}

	public final RfProvider<HttpRfSettings> getRfProvider() {
		return this.rfProvider;
	}

	@RequestMapping(
			value = "/receivers/all.json",
			method = {RequestMethod.GET, RequestMethod.HEAD})
	@ResponseBody
	public List<RfReceiverBean> allReceivers() {

		final Collection<? extends RfReceiver<?>> receivers =
				getRfStore().allRfReceivers();
		final ArrayList<RfReceiverBean> beans =
				new ArrayList<>(receivers.size());

		for (RfReceiver<?> receiver : receivers) {

			@SuppressWarnings("unchecked")
			final RfReceiverBean bean =
					new RfReceiverBean((RfReceiver<HttpRfSettings>) receiver);

			beans.add(bean);
		}

		Collections.sort(beans);

		return beans;
	}

	@RequestMapping(
			value = "/receivers/create.json",
			method = RequestMethod.PUT)
	@ResponseBody
	public RfReceiverBean createReceiver(
			HttpServletRequest request,
			@RequestBody RfReceiverBean bean)
	throws MalformedURLException {

		final RfReceiverEditor<HttpRfSettings> editor =
				getRfStore().newRfReceiver(getRfProvider());

		bean.edit(editor);
		editor.getRfSettings().setClientURL(
				new URL(applicationURL(request) + "/clr-client"));

		final RfReceiver<HttpRfSettings> receiver = editor.save();

		bean.setId(receiver.getId());

		return bean.update(receiver);
	}

	@RequestMapping(
			value = "/receivers/{id:\\d+}.json",
			method = RequestMethod.PUT)
	@ResponseBody
	public RfReceiverBean updateReceiver(
			@PathVariable("id") int id,
			@RequestBody RfReceiverBean bean)
	throws MalformedURLException {

		@SuppressWarnings("unchecked")
		final RfReceiver<HttpRfSettings> receiver =
				(RfReceiver<HttpRfSettings>) getRfStore().rfReceiverById(id);

		requireNonNull(receiver, "Unknown RFID tag store: " + id);

		final RfReceiverEditor<HttpRfSettings> editor = receiver.modify();

		bean.edit(editor);

		return bean.update(editor.save());
	}

	@RequestMapping(
			value = "/receivers/{id:\\d+}.json",
			method = RequestMethod.DELETE)
	@ResponseBody
	public RfReceiverBean deleteReceiver(@PathVariable("id") int id) {

		@SuppressWarnings("unchecked")
		final RfReceiver<HttpRfSettings> receiver =
				(RfReceiver<HttpRfSettings>) getRfStore().rfReceiverById(id);

		requireNonNull(receiver, "Unknown RFID tag store: " + id);

		receiver.delete();

		return new RfReceiverBean(receiver);
	}

	private static String applicationURL(HttpServletRequest request) {

		final String scheme = request.getScheme();
		final int port = request.getServerPort();
		final StringBuilder url = new StringBuilder();

		url.append(scheme).append("://").append(request.getServerName());
		if (!(port == 80 && "http".equals(scheme)
				|| port == 443 && "https".equals("scheme"))) {
			url.append(':').append(port);
		}
		url.append(request.getContextPath());

		return url.toString();
	}

}
