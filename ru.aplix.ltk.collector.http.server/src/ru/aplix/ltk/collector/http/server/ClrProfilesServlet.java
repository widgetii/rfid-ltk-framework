package ru.aplix.ltk.collector.http.server;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static ru.aplix.ltk.collector.http.ClrProfileId.clrProfileId;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Formatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.collector.http.ClrProfileData;
import ru.aplix.ltk.collector.http.ClrProfileId;
import ru.aplix.ltk.collector.http.ClrProfilesData;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.util.Parameterized;
import ru.aplix.ltk.core.util.Parameters;


public class ClrProfilesServlet extends HttpServlet {

	private static final long serialVersionUID = 2244925870470971820L;

	public static final String PROFILES_SERVLET_PATH = "/profiles";

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

		final ClrProfilesData profilesData = new ClrProfilesData();

		for (ProviderClrProfiles<?> providerProfiles : allProfiles()) {
			addProviderProfiles(profilesData, providerProfiles);
		}

		respond(resp, profilesData);
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

		final ClrProfileData<?> profileData =
				new ClrProfileData<>(profiles.getProvider());

		profileData.read(new Parameters(req.getParameterMap()));

		final ClrProfile<?> profile = profiles.createProfile(profileId);

		profileData.write(profile.getConfig().getParameters());
		try {
			profile.save();
		} catch (Exception e) {
			sendError(
					resp,
					SC_INTERNAL_SERVER_ERROR,
					"unexpected",
					"Unexpected error: %s",
					e.getLocalizedMessage());
		}

		resp.setStatus(SC_NO_CONTENT);
		resp.flushBuffer();
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
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

		final ClrProfile<?> profile = profiles.get(profileId.getId());

		if (profile == null) {
			sendError(
					resp,
					SC_BAD_REQUEST,
					"unknown.profile",
					"Profile does not exist: %s", profileId.toString());
			return;
		}

		profile.delete();
	}

	private static ClrProfileId profileId(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws IOException {

		final String profileIdParam = req.getParameter("profileId");

		if (profileIdParam == null) {
			sendError(
					resp,
					SC_BAD_REQUEST,
					"missing.profile_id",
					"Profile identifier not specified");
			return null;
		}

		final ClrProfileId profileId = clrProfileId(profileIdParam);

		if (profileId.getId().isEmpty()) {
			sendError(
					resp,
					SC_BAD_REQUEST,
					"invalid.profile_id",
					"Invalid profile identifier",
					profileId.toString());
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
					"unknown.provider",
					"Unsupported RFID provider: %s",
					profileId.getProviderId());
		}

		return profiles;
	}

	private static <S extends RfSettings> void addProviderProfiles(
			ClrProfilesData profilesData,
			ProviderClrProfiles<S> providerProfiles) {

		final RfProvider<S> provider = providerProfiles.getProvider();
		boolean hasProfiles = false;

		for (ClrProfile<S> profile : providerProfiles) {
			hasProfiles = true;

			final ClrProfileData<S> profileData =
					new ClrProfileData<>(provider);

			profileData.read(profile.getParameters());
			profilesData.profiles().put(profile.getProfileId(), profileData);
		}

		if (!hasProfiles) {
			addDefaultProfile(profilesData, provider);
		}
	}

	private static <S extends RfSettings> void addDefaultProfile(
			ClrProfilesData profilesData,
			RfProvider<S> provider) {

		final ClrProfileData<S> defaultData =
				new ClrProfileData<>(provider);

		profilesData.profiles().put(
				new ClrProfileId(provider.getId(), null),
				defaultData);
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
			String errorCode,
			String message,
			String... args)
	throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		resp.setStatus(httpStatus);
		resp.setHeader("X-Error-Code", errorCode);

		if (args.length != 0) {
			resp.setHeader("X-Error-Args", Integer.toString(args.length));
			for (int i = 0; i < args.length; ++i) {
				resp.setHeader("X-Error-Arg-" + i, args[i]);
			}
		}

		@SuppressWarnings("resource")
		final Formatter out = new Formatter(resp.getWriter());

		out.format(message + "\n", args);
		out.flush();
	}

}
