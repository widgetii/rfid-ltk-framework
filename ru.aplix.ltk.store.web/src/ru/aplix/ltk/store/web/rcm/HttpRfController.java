package ru.aplix.ltk.store.web.rcm;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.collector.http.ClrAddress;
import ru.aplix.ltk.collector.http.ClrError;
import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.client.*;
import ru.aplix.ltk.store.RfStore;


@Controller
public class HttpRfController {

	@Autowired
	private RfStore rfStore;

	@Autowired
	private HttpRfManager httpRfManager;

	public final RfStore rfStore() {
		return this.rfStore;
	}

	public final HttpRfManager httpRfManager() {
		return this.httpRfManager;
	}

	@RequestMapping(
			value = "/collectors/servers.json",
			method = RequestMethod.GET)
	@ResponseBody
	public HttpRfServersBean collectorServers() {

		final HttpRfServersBean servers = new HttpRfServersBean();

		servers.addReceivers(rfStore().allRfReceivers());

		return servers;
	}

	@RequestMapping(
			value = "/collector/profiles.json",
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
			result.addProfiles(server.loadProfiles());

			final ClrProfileId profileId = address.getProfileId();

			if (profileId != null) {
				result.setSelectedProfileId(profileId.urlEncode());
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
