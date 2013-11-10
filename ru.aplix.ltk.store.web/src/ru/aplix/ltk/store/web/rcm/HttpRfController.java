package ru.aplix.ltk.store.web.rcm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.aplix.ltk.store.RfStore;


@Controller
public class HttpRfController {

	@Autowired
	private RfStore rfStore;

	public final RfStore getRfStore() {
		return this.rfStore;
	}

	@RequestMapping(
			value = "/collectors/servers.json",
			method = RequestMethod.GET)
	@ResponseBody
	public HttpRfServersBean collectorServers() {

		final HttpRfServersBean servers = new HttpRfServersBean();

		servers.addReceivers(getRfStore().allRfReceivers());

		return servers;
	}

}
