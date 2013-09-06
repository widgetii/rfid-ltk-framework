package ru.aplix.ltk.collector.http.server;

import java.util.UUID;

import javax.servlet.ServletContext;

import ru.aplix.ltk.collector.http.CollectorClientRequest;


public class ClrClients {

	private final CollectorHttpService collectorService;
	private final ServletContext serlvetContext;

	public ClrClients(
			CollectorHttpService collectorService,
			ServletContext serlvetContext) {
		this.collectorService = collectorService;
		this.serlvetContext = serlvetContext;
	}

	public final CollectorHttpService getCollectorService() {
		return this.collectorService;
	}

	public final ServletContext getSerlvetContext() {
		return this.serlvetContext;
	}

	public ClrClient create(CollectorClientRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClrClient update(UUID id, CollectorClientRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(UUID id) {
		// TODO Auto-generated method stub
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}
