package ru.aplix.ltk.collector.http.server;

import static javax.servlet.http.HttpServletResponse.*;
import static ru.aplix.ltk.collector.http.ClrAddress.COLLECTOR_SERVLET_PATH;
import static ru.aplix.ltk.collector.http.ClrClientId.urlDecodeClrClientId;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.ClrClientId;
import ru.aplix.ltk.collector.http.ClrClientRequest;
import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.util.Parameters;


public class ClrClientServlet extends HttpServlet {

	private static final long serialVersionUID = -4825927568800965736L;

	private final AllClrProfiles allProfiles;

	public ClrClientServlet(AllClrProfiles allProfiles) {
		this.allProfiles = allProfiles;
	}

	public final AllClrProfiles allProfiles() {
		return this.allProfiles;
	}

	@Override
	protected void doGet(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");

		final String rootPath = rootPath(req);
		@SuppressWarnings("resource")
		final PrintWriter out = resp.getWriter();

		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Накопитель тегов RFID</title>");
		out.println("</head>");

		out.println("<body>");
		out.println("<h1>Профили оборудования</h1>");

		RfProvider<?> lastProvider = null;

		for (ClrProfile<?> profile : sortedProfiles()) {

			final RfProvider<?> provider = profile.getProvider();

			if (provider != lastProvider) {
				if (lastProvider != null) {
					out.println("<hr/>");
				}
				lastProvider = provider;

				out.append("<h2>")
				.append(html(profile.getProvider().getName()))
				.append("</h2>")
				.println();
			}

			profileReport(out, rootPath, profile);
		}

		out.println("</body>");
		out.println("</html>");
		out.flush();
	}

	@Override
	protected void doPut(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {

		final ClrClientId clientId = urlDecodeClrClientId(req.getPathInfo());
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

		final ClrClientId clientId = urlDecodeClrClientId(req.getPathInfo());

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
		url.append(request.getContextPath()).append(COLLECTOR_SERVLET_PATH);

		return url.toString();
	}

	private Collection<ClrProfile<?>> sortedProfiles() {

		final TreeMap<ClrProfileId, ClrProfile<?>> profiles = new TreeMap<>();

		for (ProviderClrProfiles<?> providerProfiles : allProfiles()) {
			for (ClrProfile<?> profile : providerProfiles) {
				profiles.put(profile.getProfileId(), profile);
			}
		}

		return profiles.values();
	}

	private void profileReport(
			PrintWriter out,
			String rootPath,
			ClrProfile<?> profile) {

		final String label = profileLabel(profile);

		out.append("<p><a href=\"")
		.append(rootPath)
		.append('/')
		.append(profile.getProfileId().urlEncode())
		.append("\">")
		.append(html(label))
		.append("</a></p>")
		.println();
	}

	private static String profileLabel(ClrProfile<?> profile) {

		final ClrProfileId profileId = profile.getProfileId();
		final String profileName = profile.getConfig().getProfileName();

		if (profileName == null) {
			return profileId.getId();
		}

		return profileName + " (" + profileId.getId() + ')';
	}

	private static String html(String text) {
		return text.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("&", "&amp;");
	}

}
