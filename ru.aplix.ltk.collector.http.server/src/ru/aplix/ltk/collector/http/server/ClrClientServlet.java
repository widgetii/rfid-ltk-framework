package ru.aplix.ltk.collector.http.server;

import static java.util.Collections.synchronizedMap;
import static javax.servlet.http.HttpServletResponse.*;
import static ru.aplix.ltk.collector.http.ClrClientId.clrClientId;
import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_CLASS;
import static ru.aplix.ltk.osgi.OSGiUtils.bundleParameters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.collector.http.*;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class ClrClientServlet extends HttpServlet {

	private static final long serialVersionUID = -4825927568800965736L;

	private static final String CONFIG_PREFIX = "ru.aplix.ltk.rf";

	private final CollectorHttpService collectorService;
	private final Map<ClrProfileId, ClrProfile> profiles =
			synchronizedMap(new HashMap<ClrProfileId, ClrProfile>());
	private RfProviders rfProviders;

	public ClrClientServlet(CollectorHttpService collectorService) {
		this.collectorService = collectorService;
	}

	public final CollectorHttpService getCollectorService() {
		return this.collectorService;
	}

	@Override
	public void init() throws ServletException {
		this.rfProviders = new RfProviders();
		this.rfProviders.open();
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

		final ClrProfile profile = this.profiles.get(profileId);

		if (profile == null) {
			resp.sendError(
					SC_NOT_FOUND,
					"RFID profile " + profileId + " does not exist");
			return;
		}

		final ClrClient client = profile.createClient(request);

		resp.setStatus(SC_CREATED);

		final ClrClientResponse result = new ClrClientResponse();

		result.setClientId(client.getId());

		writeResult(resp, result);
	}

	private void updateClient(
			HttpServletResponse resp,
			ClrClientId clientId,
			ClrClientRequest request)
	throws IOException {

		final ClrProfile profile = this.profiles.get(clientId.getProfileId());

		if (profile == null) {
			resp.sendError(
					SC_NOT_FOUND,
					"RFID profile " + clientId.getProfileId()
					+ " does not exist");
			return;
		}
		if (profile.updateClient(clientId.getUUID(), request) == null) {
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

		final ClrProfile profile = this.profiles.get(clientId.getProfileId());

		if (profile == null) {
			resp.sendError(
					SC_NOT_FOUND,
					"RFID profile " + clientId.getProfileId()
					+ " does not exist");
			return;
		}

		if (!profile.deleteClient(clientId.getUUID())) {
			resp.sendError(SC_NOT_FOUND, "Unknown client: " + clientId);
			return;
		}

		resp.setStatus(SC_NO_CONTENT);
		resp.flushBuffer();
	}

	@Override
	public void destroy() {
		this.rfProviders.close();
	}

	private void writeResult(
			HttpServletResponse resp,
			Parameterized result)
	throws IOException {

		@SuppressWarnings("resource")
		final PrintWriter out = resp.getWriter();
		final Parameters parameters = new Parameters();

		result.write(parameters);
		parameters.urlEncode(out);

		out.flush();
	}

	private void addProvider(RfProvider<?> provider) {

		final Parameters params = providerParameters(provider);
		final String[] configs = params.valuesOf("");

		if (configs == null) {
			addProfile(provider, new ClrProfileId(provider.getId()), params);
		} else {
			for (String config : configs) {
				addProfile(
						provider,
						new ClrProfileId(provider.getId(), config),
						params.sub(config));
			}
		}
	}

	private void addProfile(
			RfProvider<?> provider,
			ClrProfileId profileId,
			Parameters params) {
		this.profiles.put(profileId, new ClrProfile(provider, params));
	}

	private void removeProvider(RfProvider<?> provider) {

		final Parameters params = providerParameters(provider);
		final String[] configs = params.valuesOf("");

		if (configs == null) {
			removeProfile(new ClrProfileId(provider.getId()));
		} else {
			for (String config : configs) {
				removeProfile(new ClrProfileId(provider.getId(), config));
			}
		}
	}

	private void removeProfile(ClrProfileId profileId) {

		final ClrProfile profile = this.profiles.remove(profileId);

		if (profile != null) {
			profile.dispose();
		}
	}

	private Parameters providerParameters(RfProvider<?> provider) {
		return bundleParameters(getCollectorService().getContext())
				.sub(CONFIG_PREFIX + '.' + provider.getId());
	}

	private final class RfProviders
			extends ServiceTracker<RfProvider<?>, RfProvider<?>> {

		RfProviders() {
			super(
					getCollectorService().getContext(),
					RF_PROVIDER_CLASS,
					null);
		}

		@Override
		public RfProvider<?> addingService(
				ServiceReference<RfProvider<?>> reference) {

			final RfProvider<?> provider = super.addingService(reference);

			addProvider(provider);

			return provider;
		}

		@Override
		public void removedService(
				ServiceReference<RfProvider<?>> reference,
				RfProvider<?> service) {
			removeProvider(service);
			super.removedService(reference, service);
		}

	}

}
