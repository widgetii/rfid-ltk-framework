package ru.aplix.ltk.store.web.rcm;

import static java.util.Collections.emptyMap;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static ru.aplix.ltk.store.web.receiver.RfReceiverDesc.rfReceiverDesc;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.collector.http.ClrAddress;
import ru.aplix.ltk.collector.http.ClrError;
import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.client.*;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfStore;
import ru.aplix.ltk.store.web.ErrorBean;
import ru.aplix.ltk.store.web.ResponseBean;
import ru.aplix.ltk.store.web.rcm.ui.*;
import ru.aplix.ltk.store.web.receiver.RfReceiverDesc;


@Controller
public class RcmController {

	private static final String DEFAULT_PROVIDER_ID = "_";
	private static final Logger log =
			LoggerFactory.getLogger(RcmController.class);

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
	public HttpRfProfilesBean profiles(
			@RequestParam("server") String serverURL,
			HttpServletResponse resp) {

		final HttpRfProfilesBean result = new HttpRfProfilesBean();
		final HttpRfServer server = server(serverURL, result, resp);

		if (server == null) {
			return null;
		}

		try {
			result.loadProfiles(this, server);

			final ClrProfileId profileId = server.getAddress().getProfileId();

			if (profileId != null) {
				result.setSelected(profileId.urlEncode());
			}
		} catch (InvalidHttpRfResponseException | IOException e) {
			handleError(e, result, resp);
			result.setInvalidServer(true);
		} catch (Exception e) {
			handleError(e, result, resp);
		}

		return result;
	}

	@RequestMapping(
			value = "/rcm/profile.json",
			method = RequestMethod.GET)
	@ResponseBody
	public RcmUIResponseBean profile(
			@RequestParam("server") String serverURL,
			@RequestParam("providerId") String providerId,
			@RequestParam("profileId") String profileId,
			HttpServletResponse resp) {

		final RcmUIResponseBean result = new RcmUIResponseBean();
		final HttpRfProfile<?> profile =
				profile(serverURL, providerId, profileId, result, resp);

		if (profile == null) {
			return null;
		}
		try {
			profile.loadSettings();
			result.setProfile(profile);
			result.setSettings(uiSettings(profile));

			final ClrProfileId id = profile.getProfileId();

			if (!id.isDefault()) {

				final URL url = profile.getServer().getAddress().clientURL(id);

				result.setReceiver(receiversByURL().get(url.toExternalForm()));
			}
		} catch (Exception e) {
			handleError(e, result, resp);
		}

		return result;
	}

	@RequestMapping(
			value = "/rcm/profile.json",
			method = RequestMethod.DELETE,
			params = "providerId")
	@ResponseBody
	public ResponseBean deleteProfile(
			@RequestParam("server") String serverURL,
			@RequestParam("providerId") String providerId,
			@RequestParam("profileId") String profileId,
			@RequestParam(value = "receiverId", required = false)
			Integer receiverId,
			HttpServletResponse resp) {

		final ResponseBean result = new ResponseBean();
		final HttpRfProfile<?> profile =
				profile(serverURL, providerId, profileId, result, resp);

		try {
			profile.delete();
			deleteReceiver(receiverId, result, resp);
		} catch (Exception e) {
			handleError(e, result, resp);
		}

		return result;
	}

	@RequestMapping(
			value = "/rcm/profile.json",
			method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseBean deleteReceiver(
			@RequestParam("receiverId") int receiverId,
			HttpServletResponse resp) {

		final ResponseBean result = new ResponseBean();

		try {
			deleteReceiver(receiverId, result, resp);
		} catch (Exception e) {
			handleError(e, result, resp);
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

	<S extends RfSettings, R extends RcmUISettings> R uiSettings(
			HttpRfProfile<S> profile) {

		@SuppressWarnings("unchecked")
		final RcmUIController<S, R> uiController =
				(RcmUIController<S, R>) uiController(
						profile.getProfileId().getProviderId());

		return uiController.uiSettings(profile);
	}

	Map<String, RfReceiverDesc> receiversByURL() {

		final Collection<? extends RfReceiver<?>> list =
				rfStore().allRfReceivers();
		final HashMap<String, RfReceiverDesc> map =
				new HashMap<>(list.size());

		for (RfReceiver<?> receiver : list) {

			final RfReceiverDesc desc = rfReceiverDesc(receiver);
			final String url = desc.getRemoteURL();

			if (url != null) {

				final RfReceiverDesc old = map.put(url, desc);

				if (old != null && old.getId() < desc.getId()) {
					map.put(url, old);
				}
			}
		}

		return map;
	}

	static void handleError(
			Exception e,
			ErrorBean response,
			HttpServletResponse resp) {
		if (e instanceof InvalidHttpRfResponseException) {
			response.setError("Не похоже на сервер накопителя");
			resp.setStatus(SC_BAD_REQUEST);
			return;
		}
		if (e instanceof HttpRfServerException) {

			final HttpRfServerException ex = (HttpRfServerException) e;

			response.setError(errorMessage(ex));
			resp.setStatus(ex.getStatusCode());

			return;
		}
		log.error("RCM error", e);
		response.setError(e.toString());
		resp.setStatus(SC_INTERNAL_SERVER_ERROR);
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

	private HttpRfProfile<?> profile(
			String serverURL,
			String providerId,
			String profileId,
			ErrorBean result,
			HttpServletResponse resp) {

		final HttpRfServer server = server(serverURL, result, resp);

		if (server == null) {
			return null;
		}

		final RfProvider<?> provider = provider(providerId, result, resp);

		if (provider == null) {
			return null;
		}

		return server.profile(provider, profileId);
	}

	private HttpRfServer server(
			String serverURL,
			ErrorBean result,
			HttpServletResponse resp) {

		final ClrAddress address = address(serverURL, resp, result);

		if (address == null) {
			return null;
		}

		return httpRfManager().httpRfServer(address);
	}

	private ClrAddress address(
			String server,
			HttpServletResponse resp,
			ErrorBean result) {
		try {
			return new ClrAddress(new URL(server));
		} catch (Exception e) {
			result.setError(e.getMessage());
			resp.setStatus(SC_BAD_REQUEST);
			return null;
		}
	}

	private RfProvider<?> provider(
			String providerId,
			ErrorBean result,
			HttpServletResponse resp) {

		final RfProvider<?> provider =
				rfStore().allRfProviders().get(providerId);

		if (provider == null) {
			result.setError("Нет такого провайдера: " + providerId);
			resp.setStatus(SC_BAD_REQUEST);
		}

		return provider;
	}

	private void deleteReceiver(
			Integer receiverId,
			ErrorBean result,
			HttpServletResponse resp) {
		if (receiverId == null) {
			return;
		}

		final RfReceiver<?> receiver =
				rfStore().rfReceiverById(receiverId);

		if (receiver == null) {
			result.setError("Нет такого приёмника: " + receiverId);
			resp.setStatus(SC_BAD_REQUEST);
			return;
		}

		receiver.delete(false);
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
