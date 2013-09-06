package ru.aplix.ltk.collector.http.server;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.CollectorClientRequest;
import ru.aplix.ltk.core.util.Parameters;


public class ClrClientServlet extends HttpServlet {

	private static final long serialVersionUID = 2040846601507741882L;

	private final CollectorHttpService collectorService;
	private ClrClients clients;

	public ClrClientServlet(CollectorHttpService collectorService) {
		this.collectorService = collectorService;
	}

	public final CollectorHttpService getCollectorService() {
		return this.collectorService;
	}

	@Override
	public void init() throws ServletException {
		this.clients =
				new ClrClients(getCollectorService(), getServletContext());
	}

	@Override
	protected void doPut(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {

		final UUID id = id(req);

		final CollectorClientRequest request = new CollectorClientRequest(
				new Parameters(req.getParameterMap()));

		if (id == null) {
			createClient(resp, request);
		} else {
			updateClient(resp, id, request);
		}
	}

	private void updateClient(
			HttpServletResponse resp,
			UUID id,
			CollectorClientRequest request)
	throws IOException {
		this.clients.update(id, request);
		resp.setStatus(SC_NO_CONTENT);
		resp.flushBuffer();
	}

	private void createClient(
			HttpServletResponse resp,
			CollectorClientRequest request)
	throws IOException {

		final ClrClient client = this.clients.create(request);

		resp.setStatus(SC_CREATED);

		@SuppressWarnings("resource")
		final ServletOutputStream out = resp.getOutputStream();

		out.println(client.getId().toString());
		out.flush();
	}

	@Override
	protected void doDelete(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {

		final UUID id = id(req);

		if (id == null) {
			resp.sendError(SC_BAD_REQUEST);
		}
		// TODO Auto-generated method stub
		super.doDelete(req, resp);
	}

	@Override
	public void service(
			ServletRequest req,
			ServletResponse res)
	throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		this.clients.dispose();
	}

	private static UUID id(HttpServletRequest req) {

		String info = req.getPathInfo();

		if (info == null) {
			return null;
		}
		if (info.startsWith("/")) {
			info = info.substring(1, info.length());
		}

		final int slashIdx = info.indexOf('/');

		if (slashIdx >= 0) {
			info = info.substring(0, slashIdx);
		}

		return UUID.fromString(info);
	}

}
