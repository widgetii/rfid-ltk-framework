package ru.aplix.ltk.collector.http.client.impl;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfSource;


final class HttpRfCollector extends RfCollector {

	public HttpRfCollector(RfSource source) {
		super(source);
	}

	void updateTagAppearance(RfTagAppearanceMessage tagAppearance) {
		tagAppearanceSubscriptions().sendMessage(tagAppearance);
	}

}
