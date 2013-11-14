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

import ru.aplix.ltk.collector.http.*;
import ru.aplix.ltk.collector.http.client.*;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.web.rcm.ui.*;


@Controller
public class RcmController {

	private static final String DEFAULT_PROVIDER_ID = "_";

	@Autowired
	private RfStore rfStore;

	@Autowired
	private HttpRfManager httpRfManager;

	private HashMap<String, RcmUIController<?, ?>> uiControllers =
			new HashMap<>();

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
			result.loadProfiles(this, server);

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
			for (RcmUIController<?, ?> controller :
				this.uiControllers.values()) {
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
				new HashMap<>(this.uiControllers.size());

		for (Map.Entry<String, RcmUIController<?, ?>> e :
				this.uiControllers.entrySet()) {
			uis.put(e.getKey(), new RcmUIDesc(e.getValue()));
		}

		return uis;
	}

	@SuppressWarnings("unchecked")
	public <S extends RfSettings, U extends RcmUISettings>
	RcmUIController<S, U> uiController(String profileId) {

		final RcmUIController<?, ?> found = this.uiControllers.get(profileId);

		if (found != null) {
			return (RcmUIController<S, U>) found;
		}

		return (RcmUIController<S, U>) this.uiControllers.get(
				DEFAULT_PROVIDER_ID);
	}

	@Autowired(required = false)
	@RcmUIQualifier
	private void setUIControllers(RcmUIController<?, ?>[] uiControllers) {
		this.uiControllers.clear();

		for (RcmUIController<?, ?> uiController : uiControllers) {
			initUI(uiController);

			final RfProvider<?> rfProvider = uiController.getRfProvider();

			this.uiControllers.put(
					rfProvider != null
					? rfProvider.getId() : DEFAULT_PROVIDER_ID,
					uiController);
		}
	}

	private <S extends RfSettings, U extends RcmUISettings> void initUI(
			RcmUIController<S, U> uiController) {

		final RcmUIContextImpl<S, U> context =
				new RcmUIContextImpl<>(this, uiController);

		uiController.init(context);
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

	static String errorMessage(HttpRfServerException ex) {

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
