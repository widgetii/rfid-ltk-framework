package ru.aplix.ltk.collector.http.client;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;


public interface HttpRfProcessor {

	void updateStatus(String clientPath, RfStatusRequest status);

	void updateTagAppearance(String clientPath, RfTagAppearanceRequest data);

	void ping(String clientPath);

}
