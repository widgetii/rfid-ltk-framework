package ru.aplix.ltk.store.web.collector;

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.collector.http.client.HttpRfProcessor;
import ru.aplix.ltk.core.util.Parameters;


@Controller("rfCollectorClientController")
@RequestMapping("/clr-client")
public class RfCollectorClientController {

	@Autowired
	private HttpRfProcessor rfProcessor;

	public final HttpRfProcessor getRfProcessor() {
		return this.rfProcessor;
	}

	@RequestMapping(
			method = RequestMethod.POST,
			params = {"client", "type=status"})
	public void updateStatus(
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException {

		final UUID clientUUID = UUID.fromString(request.getParameter("client"));
		final RfStatusRequest statusRequest =
				new RfStatusRequest(new Parameters(request.getParameterMap()));

		getRfProcessor().updateStatus(clientUUID, statusRequest);

		emptyResponse(response);
	}

	@RequestMapping(
			method = RequestMethod.GET,
			params = "client")
	public void ping(
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException {

		final UUID clientUUID = UUID.fromString(request.getParameter("client"));

		getRfProcessor().ping(clientUUID);

		emptyResponse(response);
	}

	@RequestMapping(
			method = RequestMethod.POST,
			params = {"client", "type=tag"})
	public void updateTagAppearance(
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException {

		final UUID clientUUID = UUID.fromString(request.getParameter("client"));
		final RfTagAppearanceRequest tagAppearanceRequest =
				new RfTagAppearanceRequest(
						new Parameters(request.getParameterMap()));

		getRfProcessor().updateTagAppearance(clientUUID, tagAppearanceRequest);

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
