package ru.aplix.ltk.collector.http.client;

import java.util.UUID;

import ru.aplix.ltk.collector.http.RfStatusRequest;
import ru.aplix.ltk.collector.http.RfTagAppearanceRequest;


public interface HttpRfProcessor {

	void updateStatus(UUID clientUUID, RfStatusRequest status);

	void updateTagAppearance(UUID clientUUID, RfTagAppearanceRequest data);

	void ping(UUID clientUUID);

}
