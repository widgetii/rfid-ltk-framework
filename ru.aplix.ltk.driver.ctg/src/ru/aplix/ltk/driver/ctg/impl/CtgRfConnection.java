package ru.aplix.ltk.driver.ctg.impl;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.source.RfSource;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;


public class CtgRfConnection extends RfConnection {

	private final LLRPReaderId readerId;
	private final CtgRfSettings settings;
	private final CtgRfProvider provider;

	public CtgRfConnection(CtgRfProvider provider, CtgRfSettings settings) {
		super(settings);
		this.provider = provider;
		this.readerId = new LLRPReaderId(settings);
		this.settings = settings;
	}

	public final CtgRfProvider getProvider() {
		return this.provider;
	}

	public final LLRPReaderId getReaderId() {
		return this.readerId;
	}

	public final CtgRfSettings getSettings() {
		return this.settings;
	}

	@Override
	protected RfSource createSource() {
		return getProvider().driverFor(this).getReader().toRfSource();
	}

}
