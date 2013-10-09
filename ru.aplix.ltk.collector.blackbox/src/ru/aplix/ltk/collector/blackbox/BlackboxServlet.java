package ru.aplix.ltk.collector.blackbox;

import static org.osgi.framework.Constants.OBJECTCLASS;
import static ru.aplix.ltk.core.RfProvider.RF_PROVIDER_ID;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;
import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.core.util.Parameters;
import ru.aplix.ltk.driver.blackbox.BlackboxRfSettings;
import ru.aplix.ltk.driver.blackbox.ManualRfTracker;
import ru.aplix.ltk.message.MsgConsumer;


class BlackboxServlet extends HttpServlet {

	private static final long serialVersionUID = 4506781575156128894L;

	public static final String BLACKBOX_SERVLET_PATH = "/blackbox";

	private final CollectorBlackbox blackbox;
	private BlackboxTracker blackboxTracker;

	private ManualRfTracker rfTracker;

	private RfCollectorHandle handle;

	BlackboxServlet(CollectorBlackbox blackbox) {
		this.blackbox = blackbox;
	}

	@Override
	public void init() throws ServletException {
		try {
			this.blackboxTracker =
					new BlackboxTracker(this.blackbox.getContext());
		} catch (InvalidSyntaxException e) {
			throw new ServletException(
					"Failed to initialize blackbox servlet",
					e);
		}

		this.blackboxTracker.open();
	}

	@Override
	protected void doPost(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {

		final String path = req.getPathInfo();

		if (pathPrefix(path, "/tag")) {
			updateTagAppearance(req, resp);
			return;
		}
		if (pathPrefix(path, "/status")) {
			updateStatus(req, resp);
			return;
		}

		super.doPost(req, resp);
	}

	@Override
	public void destroy() {
		this.blackboxTracker.close();
	}

	private void connect(RfProvider<BlackboxRfSettings> provider) {
		try {
			this.rfTracker = new ManualRfTracker();

			final BlackboxRfSettings settings = provider.newSettings();

			settings.setTracker(this.rfTracker);

			this.handle =
					provider.connect(settings)
					.getCollector()
					.subscribe(new DummyStatusListener());
		} catch (Throwable e) {
			this.rfTracker = null;
			getServletContext().log("Failed to connect to blackbox driver", e);
		}
	}

	private void disconnect() {
		this.rfTracker = null;
		if (this.handle != null) {
			this.handle.unsubscribe();
			this.handle = null;
		}
	}

	private void updateTagAppearance(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {
		if (this.rfTracker == null) {
			super.doPost(req, resp);
			return;
		}

		final RfTagAppearanceRequest tagAppearance =
				new RfTagAppearanceRequest(
						new Parameters(req.getParameterMap()));

		this.rfTracker.updateTagAppearance(tagAppearance);
	}

	private void updateStatus(
			HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, IOException {
		if (this.rfTracker == null) {
			super.doPost(req, resp);
			return;
		}

		final RfStatusRequest status =
				new RfStatusRequest(new Parameters(req.getParameterMap()));

		this.rfTracker.updateStatus(status);
	}

	private static boolean pathPrefix(String path, String prefix) {
		if (path.equals(prefix)) {
			return true;
		}
		return path.startsWith(prefix + '/');
	}

	private static Filter blackboxFilter(
			BundleContext context)
	throws InvalidSyntaxException {
		return context.createFilter(
				"(&(" + OBJECTCLASS + '=' + RfProvider.class.getName()
				+ ")(" + RF_PROVIDER_ID + "=blackbox))");
	}

	private final class BlackboxTracker
			extends ServiceTracker<
					RfProvider<BlackboxRfSettings>,
					RfProvider<BlackboxRfSettings>> {

		BlackboxTracker(BundleContext context) throws InvalidSyntaxException {
			super(
					context,
					blackboxFilter(context), null);
		}

		@Override
		public RfProvider<BlackboxRfSettings> addingService(
				ServiceReference<RfProvider<BlackboxRfSettings>> reference) {

			final RfProvider<BlackboxRfSettings> provider =
					super.addingService(reference);

			connect(provider);

			return provider;
		}

		@Override
		public void removedService(
				ServiceReference<RfProvider<BlackboxRfSettings>> reference,
				RfProvider<BlackboxRfSettings> service) {
			disconnect();
			super.removedService(reference, service);
		}

	}

	private static final class DummyStatusListener
			implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

		@Override
		public void consumerSubscribed(RfCollectorHandle handle) {
			handle.requestTagAppearance(new DummyTagListener());
		}

		@Override
		public void messageReceived(RfStatusMessage message) {
		}

		@Override
		public void consumerUnsubscribed(RfCollectorHandle handle) {
		}

	}

	private static final class DummyTagListener
			implements MsgConsumer<
					RfTagAppearanceHandle,
					RfTagAppearanceMessage> {

		@Override
		public void consumerSubscribed(RfTagAppearanceHandle handle) {
		}

		@Override
		public void messageReceived(RfTagAppearanceMessage message) {
		}

		@Override
		public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
		}

	}

}
