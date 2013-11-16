package ru.aplix.ltk.store.web.rcm.ui;

import javax.servlet.http.HttpServletResponse;

import ru.aplix.ltk.core.RfSettings;


public interface RcmUIContext<
		S extends RfSettings,
		U extends RcmUISettings<S>> {

	RcmUIResponseBean save(
			String serverURL,
			U settings,
			HttpServletResponse resp);

}
