package ru.aplix.ltk.store.web.rcm;

import static java.util.Collections.emptyMap;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.collector.http.ClrAddress;
import ru.aplix.ltk.collector.http.ClrError;
import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.client.*;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.web.rcm.ui.RcmUIContext;
import ru.aplix.ltk.store.web.rcm.ui.RcmUIController;
import ru.aplix.ltk.store.web.rcm.ui.RcmUIQualifier;


@Controller
public class RcmController implements RcmUIContext {

	@Autowired
	private RfStore rfStore;

	@Autowired
	private HttpRfManager httpRfManager;

	private RcmUIController<?, ?>[] uiControllers;

	public final RfStore rfStore() {
		return this.rfStore;
	}

	public final HttpRfManager httpRfManager() {
		return this.httpRfManager;
	}

	@RequestMapping(
			value = "/rcm/servers.json",
			method = RequestMethod.GET)
	@ResponseBody
	public HttpRfServersBean collectorServers() {

		final HttpRfServersBean servers = new HttpRfServersBean();

		servers.addReceivers(rfStore().allRfReceivers());

		return servers;
	}

	@RequestMapping(
			value = "/rcm/profiles.json",
			method = RequestMethod.GET)
	@ResponseBody
	public HttpRfProfilesBean findProfiles(
			@RequestParam("server") String serverURL,
			HttpServletResponse resp) {

		final HttpRfProfilesBean result = new HttpRfProfilesBean();
		final ClrAddress address = address(serverURL, resp, result);

		if (address == null) {
			return result;
		}

		final HttpRfServer server = httpRfManager().httpRfServer(address);

		try {
			result.loadProfiles(server, rfStore());

			final ClrProfileId profileId = address.getProfileId();

			if (profileId != null) {
				result.setSelected(profileId.urlEncode());
			}
		} catch (InvalidHttpRfResponseException e) {
			result.setError("Не похоже на сервер накопителя");
			result.setInvalidServer(true);
			resp.setStatus(SC_BAD_REQUEST);
		} catch (HttpRfServerException e) {
			result.setError(errorMessage(e));
			resp.setStatus(e.getStatusCode());
		} catch (IOException e) {
			result.setError(e.toString());
			result.setInvalidServer(true);
			resp.setStatus(SC_INTERNAL_SERVER_ERROR);
		}

		return result;
	}

	@RequestMapping(
			value = "/rcm/ui-scripts.json",
			method = RequestMethod.GET)
	@ResponseBody
	public RcmUIScriptDesc uiScripts() {

		final RcmUIScriptDesc desc = new RcmUIScriptDesc();

		if (this.uiControllers != null) {
			for (RcmUIController<?, ?> controller : this.uiControllers) {
				desc.add(controller);
			}
		}

		return desc;
	}

	@RequestMapping(
			value = "/rcm/ui.json",
			method = RequestMethod.GET)
	@ResponseBody
	public Map<String, RcmUIDesc> ui() {
		if (this.uiControllers == null) {
			return emptyMap();
		}

		final HashMap<String, RcmUIDesc> uis =
				new HashMap<>(this.uiControllers.length);

		for (RcmUIController<?, ?> controller : this.uiControllers) {

			final RfProvider<?> rfProvider = controller.getRfProvider();

			uis.put(
					rfProvider != null ? rfProvider.getId() : "_",
					new RcmUIDesc(controller));
		}

		return uis;
	}

	@Autowired(required = false)
	@RcmUIQualifier
	private void setUIControllers(RcmUIController<?, ?>[] uiControllers) {
		this.uiControllers = uiControllers;
		for (RcmUIController<?, ?> controller : uiControllers) {
			controller.init(this);
		}
	}

	private ClrAddress address(
			String server,
			HttpServletResponse resp,
			HttpRfProfilesBean result) {
		try {
			return new ClrAddress(new URL(server));
		} catch (Exception e) {
			result.setError(e.getMessage());
			resp.setStatus(SC_BAD_REQUEST);
			return null;
		}
	}

	private static String errorMessage(HttpRfServerException ex) {

		final ClrError error = ex.getError();

		switch (error) {
		case MISSING_PROFILE_ID:
			return "Профиль не указан";
		case INVALID_PROFILE_ID:
			return "Неверный идентификатор профиля";
		case UNKNOWN_PROFILE:
			return error.format("Неизвестный профиль: %s", ex.getErrorArgs());
		case UNKNOWN_PROVIDER:
			return error.format("Неизвестный провайдер: %s", ex.getErrorArgs());
		case UNEXPECTED:
		}

		return error.format("Ошибка: %s", ex.getErrorArgs());
	}

}
