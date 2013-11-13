package ru.aplix.ltk.store.web.rcm;

import java.util.ArrayList;
import java.util.List;

import ru.aplix.ltk.store.web.rcm.ui.RcmUIController;


public class RcmUIScriptDesc {

	private final ArrayList<String> scriptURLs = new ArrayList<>();
	private final ArrayList<String> angularModuleIds = new ArrayList<>();

	RcmUIScriptDesc() {
	}

	public List<String> getScriptURLs() {
		return this.scriptURLs;
	}

	public List<String> getAngularModuleIds() {
		return this.angularModuleIds;
	}

	public void add(RcmUIController<?, ?> ui) {
		for (String url : ui.getScriptURLs()) {
			this.scriptURLs.add(url);
		}
		for (String id : ui.getAngularModuleIds()) {
			this.angularModuleIds.add(id);
		}
	}

}
