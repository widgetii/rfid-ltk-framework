package ru.aplix.ltk.store.web.collector;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
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
public class RfCollectorClientController {

	@Autowired
	private HttpRfProcessor rfProcessor;

	public final HttpRfProcessor getRfProcessor() {
		return this.rfProcessor;
	}

	@RequestMapping(
			value = "/clr-client",
			method = {RequestMethod.GET, RequestMethod.POST})
	public void process(
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException {

		final UUID clientUUID = UUID.fromString(request.getParameter("client"));

		if ("GET".equals(request.getMethod())) {
			getRfProcessor().ping(clientUUID);
		} else {

			final String type = request.getParameter("type");
			final Parameters params =
					new Parameters(request.getParameterMap());

			if ("tag".equals(type)) {

				final RfTagAppearanceRequest tagAppearanceRequest =
						new RfTagAppearanceRequest(params);

				getRfProcessor().updateTagAppearance(
						clientUUID,
						tagAppearanceRequest);
			} else if ("status".equals(type)) {

				final RfStatusRequest statusRequest =
						new RfStatusRequest(params);

				getRfProcessor().updateStatus(clientUUID, statusRequest);
			} else {
				response.sendError(
						SC_BAD_REQUEST,
						"Unknown type of request: " + type);
			}
		}

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
