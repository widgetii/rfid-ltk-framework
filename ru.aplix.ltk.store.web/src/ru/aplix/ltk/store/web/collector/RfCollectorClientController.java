package ru.aplix.ltk.store.web.collector;

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static ru.aplix.ltk.collector.http.CollectorHttpConstants.CLR_HTTP_CLIENT_PING_PATH;
import static ru.aplix.ltk.collector.http.CollectorHttpConstants.CLR_HTTP_CLIENT_STATUS_PATH;
import static ru.aplix.ltk.collector.http.CollectorHttpConstants.CLR_HTTP_CLIENT_TAG_PATH;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.HttpRfProcessor;
import ru.aplix.ltk.core.util.Parameters;


@Controller("rfCollectorClientController")
@RequestMapping(
		value = "/clr-client/{clientId}",
		method = RequestMethod.GET)
public class RfCollectorClientController {

	@Autowired
	private HttpRfProcessor rfProcessor;

	public final HttpRfProcessor getRfProcessor() {
		return this.rfProcessor;
	}

	@RequestMapping(
			value = CLR_HTTP_CLIENT_STATUS_PATH,
			method = RequestMethod.POST)
	public void updateStatus(
			@PathVariable("clientId") String clientId,
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException {

		final RfStatusRequest statusRequest = new RfStatusRequest(
				new Parameters(request.getParameterMap()));

		getRfProcessor().updateStatus(clientId, statusRequest);
		emptyResponse(response);
	}

	@RequestMapping(
			value = CLR_HTTP_CLIENT_PING_PATH,
			method = RequestMethod.GET)
	@ResponseBody
	public void ping(
			@PathVariable("clientId") String clientId,
			HttpServletResponse response)
	throws IOException {
		getRfProcessor().ping(clientId);
		emptyResponse(response);
	}

	@RequestMapping(
			value = CLR_HTTP_CLIENT_TAG_PATH,
			method = RequestMethod.POST)
	public void updateTagAppearance(
			@PathVariable("clientId") String clientId,
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException {

		final RfTagAppearanceRequest tagAppearanceRequest =
				new RfTagAppearanceRequest(
						new Parameters(request.getParameterMap()));

		getRfProcessor().updateTagAppearance(clientId, tagAppearanceRequest);
		emptyResponse(response);
	}

	private void emptyResponse(
			HttpServletResponse response)
	throws IOException {
		response.setContentLength(0);
		response.setStatus(SC_NO_CONTENT);
		response.flushBuffer();
	}

}
