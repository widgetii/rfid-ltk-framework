package ru.aplix.ltk.store.web.rcm;

import ru.aplix.ltk.core.RfProvider;


public class RfProviderDesc {

	private final String id;
	private final String name;

	public RfProviderDesc(RfProvider<?> provider) {
		this.id = provider.getId();
		this.name = provider.getName();
	}

	public RfProviderDesc(String id) {
		this.id = id;
		this.name = id;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

}
