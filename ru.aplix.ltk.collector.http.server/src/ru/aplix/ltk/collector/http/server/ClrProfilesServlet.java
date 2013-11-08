package ru.aplix.ltk.collector.http.server;

import java.io.IOException;
import java.io.PrintWriter;

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

}
