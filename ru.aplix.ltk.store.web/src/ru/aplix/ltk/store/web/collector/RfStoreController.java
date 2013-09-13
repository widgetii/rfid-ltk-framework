package ru.aplix.ltk.store.web.collector;

import static java.util.Objects.requireNonNull;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.RfStoreEditor;
import ru.aplix.ltk.store.RfStoreService;


@Controller("rfStoreController")
public class RfStoreController {

	@Autowired
	private RfStoreService storeService;

	@Autowired
	@Qualifier("httpRfProvider")
	private RfProvider<HttpRfSettings> provider;

	public final RfStoreService getStoreService() {
		return this.storeService;
	}

	public final RfProvider<HttpRfSettings> getProvider() {
		return this.provider;
	}

	@RequestMapping(
			value = "/stores/all.do",
			method = {RequestMethod.GET, RequestMethod.HEAD})
	@ResponseBody
	public RfStoreListBean allStores() {
		return new RfStoreListBean(getStoreService().allRfStores());
	}

	@RequestMapping(
			value = "/stores/create.do",
			method = RequestMethod.PUT)
	@ResponseBody
	public RfStoreBean createStore(
			HttpServletRequest request,
			RfStoreBean bean)
	throws MalformedURLException {

		final RfStoreEditor<HttpRfSettings> editor =
				getStoreService().newRfStore(getProvider());

		bean.edit(editor);
		editor.getRfSettings().setClientURL(
				new URL(applicationURL(request) + "/clr-client"));

		final RfStore<HttpRfSettings> store = editor.save();

		bean.setId(store.getId());

		return bean.update(store);
	}

	@RequestMapping(
			value = "/stores/{id:\\d+}.do",
			method = RequestMethod.PUT)
	@ResponseBody
	public RfStoreBean updateStore(
			@PathVariable("id") int id,
			RfStoreBean bean)
	throws MalformedURLException {

		@SuppressWarnings("unchecked")
		final RfStore<HttpRfSettings> store =
				(RfStore<HttpRfSettings>) getStoreService().rfStoreById(id);

		requireNonNull(store, "Unknown RFID tag store: " + id);

		final RfStoreEditor<HttpRfSettings> editor = store.modify();

		bean.edit(editor);

		return bean.update(editor.save());
	}

	@RequestMapping(
			value = "/stores/{id:\\d+}.do",
			method = RequestMethod.DELETE)
	public void deleteStore(@PathVariable("id") int id) {

		final RfStore<?> store = getStoreService().rfStoreById(id);

		if (store != null) {
			store.delete();
		}
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
