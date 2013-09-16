package ru.aplix.ltk.collector.http.server;

import static javax.servlet.http.HttpServletResponse.*;
import static org.apache.http.client.utils.URLEncodedUtils.CONTENT_TYPE;
import static ru.aplix.ltk.collector.http.ClrClientId.clrClientId;
import static ru.aplix.ltk.core.util.Parameters.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.*;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class ClrClientServlet extends HttpServlet {

	public static final String CLR_SERVLET_PATH = "/collector";

	private static final long serialVersionUID = -4825927568800965736L;

	private final AllClrProfiles allProfiles;

	public ClrClientServlet(CollectorHttpService collectorService) {
		this.allProfiles = new AllClrProfiles(collectorService);
	}

	public final AllClrProfiles allProfiles() {
		return this.allProfiles;
	}

	@Override
	public void init() throws ServletException {
		allProfiles().init();
	}

	@Override
	protected void doGet(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");

		@SuppressWarnings("resource")
		final PrintWriter out = resp.getWriter();

		allProfiles().statusReport(
				out,
				req.getContextPath() + CLR_SERVLET_PATH);

		out.flush();
	}

	@Override
	protected void doPut(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {

		final ClrClientId clientId = clrClientId(req.getPathInfo());
		final ClrClientRequest request =
				new ClrClientRequest(
						new Parameters(req.getParameterMap()));

		if (clientId == null) {
			resp.sendError(
					SC_BAD_REQUEST,
					"RFID profile not specified");
			return;
		}
		if (clientId.getUUID() == null) {
			createClient(resp, clientId.getProfileId(), request);
		} else {
			updateClient(resp, clientId, request);
		}
	}

	private void createClient(
			HttpServletResponse resp,
			ClrProfileId profileId,
			ClrClientRequest request)
	throws IOException {

		final ClrProfile<?> profile = allProfiles().get(profileId);

		if (profile == null) {
			resp.sendError(
					SC_NOT_FOUND,
					"RFID profile " + profileId + " does not exist");
			return;
		}

		final ClrClient<?> client = profile.connectClient(request);

		resp.setStatus(SC_CREATED);

		final ClrClientResponse result = new ClrClientResponse();

		result.setClientUUID(client.getId().getUUID());

		writeResult(resp, result);
	}

	private void updateClient(
			HttpServletResponse resp,
			ClrClientId clientId,
			ClrClientRequest request)
	throws IOException {

		final ClrProfile<?> profile =
				allProfiles().get(clientId.getProfileId());

		if (profile == null) {
			resp.sendError(
					SC_NOT_FOUND,
					"RFID profile " + clientId.getProfileId()
					+ " does not exist");
			return;
		}
		if (profile.reconnectClient(clientId.getUUID(), request) == null) {
			resp.sendError(SC_NOT_FOUND, "Unknown client: " + clientId);
			return;
		}

		resp.setStatus(SC_NO_CONTENT);
		resp.flushBuffer();
	}

	@Override
	protected void doDelete(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {

		final ClrClientId clientId = clrClientId(req.getPathInfo());

		if (clientId == null) {
			resp.sendError(
					SC_BAD_REQUEST,
					"RFID profile not specified");
			return;
		}

		final ClrProfile<?> profile =
				allProfiles().get(clientId.getProfileId());

		if (profile == null) {
			resp.sendError(
					SC_NOT_FOUND,
					"RFID profile " + clientId.getProfileId()
					+ " does not exist");
			return;
		}

		if (!profile.disconnectClient(clientId.getUUID())) {
			resp.sendError(SC_NOT_FOUND, "Unknown client: " + clientId);
			return;
		}

		resp.setStatus(SC_NO_CONTENT);
		resp.flushBuffer();
	}

	@Override
	public void destroy() {
		allProfiles().destroy();
	}

	private void writeResult(
			HttpServletResponse resp,
			Parameterized result)
	throws IOException {

		final String response = new Parameters().setBy(result).urlEncode();

		resp.setContentType(CONTENT_TYPE);
		resp.setCharacterEncoding(UTF_8);
		resp.setContentLength(response.length());

		@SuppressWarnings("resource")
		final ServletOutputStream out = resp.getOutputStream();

		out.print(response);
		out.flush();
	}

}
