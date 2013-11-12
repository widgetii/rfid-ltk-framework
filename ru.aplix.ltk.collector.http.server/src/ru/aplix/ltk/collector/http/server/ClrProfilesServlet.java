package ru.aplix.ltk.collector.http.server;

import static javax.servlet.http.HttpServletResponse.*;
import static ru.aplix.ltk.collector.http.ClrClientId.urlDecodeClrClientId;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Formatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.*;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class ClrProfilesServlet extends HttpServlet {

	private static final long serialVersionUID = 2244925870470971820L;

	private final AllClrProfiles allProfiles;

	public ClrProfilesServlet(AllClrProfiles allProfiles) {
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
		if (req.getPathInfo() != null) {
			sendProfile(req, resp);
		} else {
			sendAllProfiles(resp);
		}
	}

	@Override
	protected void doPut(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {

		final ClrProfileId profileId = profileId(req, resp);

		if (profileId == null) {
			return;
		}

		final ProviderClrProfiles<?> profiles =
				providerProfiles(resp, profileId);

		if (profiles == null) {
			return;
		}

		final ClrProfileSettings<?> profileSettings =
				new ClrProfileSettings<>(profiles.getProvider());

		profileSettings.read(new Parameters(req.getParameterMap()));

		final ClrProfile<?> profile = profiles.createProfile(profileId);

		profileSettings.write(profile.getConfig().getParameters());
		try {
			profile.save();
		} catch (Exception e) {
			sendError(
					resp,
					SC_INTERNAL_SERVER_ERROR,
					ClrError.UNEXPECTED,
					e.getLocalizedMessage());
		}

		resp.setStatus(SC_CREATED);
		resp.flushBuffer();
	}

	@Override
	protected void doDelete(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {

		final ClrProfile<?> profile = profile(req, resp);

		if (profile == null) {
			return;
		}

		profile.delete();

		resp.setStatus(SC_NO_CONTENT);
		resp.flushBuffer();
	}

	private void sendProfile(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws IOException {

		final ClrProfile<?> profile = profile(req, resp);

		if (profile == null) {
			return;
		}

		respond(resp, profileSettings(profile));
	}

	private void sendAllProfiles(HttpServletResponse resp) throws IOException {

		final ClrProfiles profiles = new ClrProfiles();

		for (ProviderClrProfiles<?> providerProfiles : allProfiles()) {
			addProviderProfiles(profiles, providerProfiles);
		}

		respond(resp, profiles);
	}

	private ClrProfile<?> profile(
			HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

		final ClrProfileId profileId = profileId(req, resp);

		if (profileId == null) {
			return null;
		}

		final ProviderClrProfiles<?> profiles =
				providerProfiles(resp, profileId);

		if (profiles == null) {
			return null;
		}

		final ClrProfile<?> profile = profiles.get(profileId.getId());

		if (profile == null) {
			sendError(
					resp,
					SC_BAD_REQUEST,
					ClrError.UNKNOWN_PROFILE,
					profileId.toString());
			return null;
		}

		return profile;
	}

	private static ClrProfileId profileId(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws IOException {

		final ClrClientId clientId = urlDecodeClrClientId(req.getPathInfo());

		if (clientId == null) {
			sendError(
					resp,
					SC_BAD_REQUEST,
					ClrError.MISSING_PROFILE_ID);
			return null;
		}

		final ClrProfileId profileId = clientId.getProfileId();

		if (clientId.getUUID() != null || profileId.getId().isEmpty()) {
			sendError(
					resp,
					SC_BAD_REQUEST,
					ClrError.INVALID_PROFILE_ID,
					clientId.toString());
			return null;
		}

		return profileId;
	}

	private ProviderClrProfiles<?> providerProfiles(
			HttpServletResponse resp,
			ClrProfileId profileId)
	throws IOException {

		final ProviderClrProfiles<?> profiles =
				allProfiles().providerProfiles(profileId.getProviderId());

		if (profiles == null) {
			sendError(
					resp,
					SC_BAD_REQUEST,
					ClrError.UNKNOWN_PROVIDER,
					profileId.getProviderId());
		}

		return profiles;
	}

	private static <S extends RfSettings> void addProviderProfiles(
			ClrProfiles profiles,
			ProviderClrProfiles<S> providerProfiles) {

		final RfProvider<S> provider = providerProfiles.getProvider();

		for (ClrProfile<S> profile : providerProfiles) {
			profiles.profiles().put(
					profile.getProfileId(),
					profileSettings(profile));
		}

		addDefaultProfile(profiles, provider);
	}

	private static <S extends RfSettings>
	ClrProfileSettings<S> profileSettings(ClrProfile<S> profile) {

		final ClrProfileSettings<S> profileSettings =
				new ClrProfileSettings<>(profile.getProvider());

		profileSettings.read(profile.getParameters());

		return profileSettings;
	}

	private static <S extends RfSettings> void addDefaultProfile(
			ClrProfiles profilesData,
			RfProvider<S> provider) {

		final ClrProfileSettings<S> defaultSettings =
				new ClrProfileSettings<>(provider);

		profilesData.profiles().put(
				new ClrProfileId(provider.getId(), null),
				defaultSettings);
	}

	private static void respond(
			HttpServletResponse resp,
			Parameterized response)
	throws IOException {

		final Parameters parameters = new Parameters();

		response.write(parameters);

		resp.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
		@SuppressWarnings("resource")
		final PrintWriter out = resp.getWriter();

		parameters.urlEncode(out);
		out.flush();
	}

	private static void sendError(
			HttpServletResponse resp,
			int httpStatus,
			ClrError error,
			String... args)
	throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		resp.setStatus(httpStatus);
		resp.setHeader("X-Error-Code", error.code());

		for (String arg : args) {
			resp.addHeader("X-Error-Arg", arg);
		}

		@SuppressWarnings("resource")
		final Formatter out = new Formatter(resp.getWriter());

		out.format(error.defaultMessage(), args);
		out.flush();
	}

}
