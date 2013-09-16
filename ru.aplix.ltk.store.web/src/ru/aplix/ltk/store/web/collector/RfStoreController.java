package ru.aplix.ltk.store.web.collector;

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


@Controller("rfStoreController")
public class RfStoreController {

	@Autowired
	private RfStore rfStore;

	@Autowired
	@Qualifier("httpRfProvider")
	private RfProvider<HttpRfSettings> provider;

	public final RfStore getRfService() {
		return this.rfStore;
	}

	public final RfProvider<HttpRfSettings> getProvider() {
		return this.provider;
	}

	@RequestMapping(
			value = "/stores/all.json",
			method = {RequestMethod.GET, RequestMethod.HEAD})
	@ResponseBody
	public List<RfStoreBean> allStores() {

		final Collection<? extends RfReceiver<?>> receivers =
				getRfService().allRfReceivers();
		final ArrayList<RfStoreBean> beans =
				new ArrayList<>(receivers.size());

		for (RfReceiver<?> receiver : receivers) {

			@SuppressWarnings("unchecked")
			final RfStoreBean bean =
					new RfStoreBean((RfReceiver<HttpRfSettings>) receiver);

			beans.add(bean);
		}

		Collections.sort(beans);

		return beans;
	}

	@RequestMapping(
			value = "/stores/create.json",
			method = RequestMethod.PUT)
	@ResponseBody
	public RfStoreBean createStore(
			HttpServletRequest request,
			@RequestBody RfStoreBean bean)
	throws MalformedURLException {

		final RfReceiverEditor<HttpRfSettings> editor =
				getRfService().newRfReceiver(getProvider());

		bean.edit(editor);
		editor.getRfSettings().setClientURL(
				new URL(applicationURL(request) + "/clr-client"));

		final RfReceiver<HttpRfSettings> store = editor.save();

		bean.setId(store.getId());

		return bean.update(store);
	}

	@RequestMapping(
			value = "/stores/{id:\\d+}.json",
			method = RequestMethod.PUT)
	@ResponseBody
	public RfStoreBean updateStore(
			@PathVariable("id") int id,
			@RequestBody RfStoreBean bean)
	throws MalformedURLException {

		@SuppressWarnings("unchecked")
		final RfReceiver<HttpRfSettings> store =
				(RfReceiver<HttpRfSettings>) getRfService().rfReceiverById(id);

		requireNonNull(store, "Unknown RFID tag store: " + id);

		final RfReceiverEditor<HttpRfSettings> editor = store.modify();

		bean.edit(editor);

		return bean.update(editor.save());
	}

	@RequestMapping(
			value = "/stores/{id:\\d+}.json",
			method = RequestMethod.DELETE)
	@ResponseBody
	public RfStoreBean deleteStore(@PathVariable("id") int id) {

		@SuppressWarnings("unchecked")
		final RfReceiver<HttpRfSettings> store =
				(RfReceiver<HttpRfSettings>) getRfService().rfReceiverById(id);

		requireNonNull(store, "Unknown RFID tag store: " + id);

		store.delete();

		return new RfStoreBean(store);
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
