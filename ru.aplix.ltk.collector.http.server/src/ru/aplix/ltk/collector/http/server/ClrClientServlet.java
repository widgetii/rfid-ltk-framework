package ru.aplix.ltk.collector.http.server;

import static javax.servlet.http.HttpServletResponse.*;
import static ru.aplix.ltk.collector.http.ClrClientId.clrClientId;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.ClrClientId;
import ru.aplix.ltk.collector.http.ClrClientRequest;
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

		allProfiles().statusReport(out, rootPath(req));

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

		createClient(resp, clientId, request);
	}

	private void createClient(
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

		final UUID clientUUID = clientId.getUUID();

		if (clientUUID == null) {
			resp.sendError(
					SC_BAD_REQUEST,
					"RFID profile client identifier not specified");
			return;
		}

		profile.connectClient(clientUUID, request);
		resp.setStatus(SC_CREATED);
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

	private static String rootPath(HttpServletRequest request) {

		final String scheme = request.getScheme();
		final int port = request.getServerPort();
		final StringBuilder url = new StringBuilder();

		url.append(scheme).append("://").append(request.getServerName());
		if (!(port == 80 && "http".equals(scheme)
				|| port == 443 && "https".equals("scheme"))) {
			url.append(':').append(port);
		}
		url.append(request.getContextPath()).append(CLR_SERVLET_PATH);

		return url.toString();
	}

}
